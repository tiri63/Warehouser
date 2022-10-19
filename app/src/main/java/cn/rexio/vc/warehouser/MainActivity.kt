package cn.rexio.vc.warehouser

import HiroUtils
import SharedPref
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.rexio.vc.warehouser.databinding.MainLayoutBinding
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : Activity() {

    private lateinit var bi: MainLayoutBinding

    var username: String? = null
    private var secret: String? = null
    private var searchMethod = 0
    private lateinit var mRecyclerAdapter: ShelfAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar.let { it?.hide() }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        bi = MainLayoutBinding.inflate(layoutInflater)
        HiroUtils.mainWindowContext = this
        setContentView(bi.root)
        initalize_Components()
        HiroUtils.setStatusBarColor(window, resources)
        loadSettings()
        initialzeData()
    }

    fun loadSettings() {
        HiroUtils.userName = SharedPref(this, "username", "null").getValue(this, HiroUtils::userName)
        HiroUtils.userToken = SharedPref(this, "secret", "null").getValue(this, HiroUtils::userToken)
        HiroUtils.userNickName = SharedPref(this, "nickname", "null").getValue(this, HiroUtils::userNickName)
        if (HiroUtils.userName.equals("null") || HiroUtils.userToken.equals("null")) {
            login()
        } else
            tryLogin(HiroUtils.userName, HiroUtils.userToken)
        HiroUtils.sendRequest(
            "/usage", arrayListOf(), arrayListOf(),
            {
                try {
                    val json = JSONObject(it)
                    when (json["ret"]) {
                        "0" -> {
                            //parse Usage
                            val ja = JSONArray(json["msg"].toString())
                            HiroUtils.usageArray.clear()
                            for (i in 0 until ja.length()) {
                                val jai = ja[i] as JSONObject
                                HiroUtils.usageArray.add(HiroUtils.Usage(jai["id"].toString().toInt(),jai["name"].toString(),jai["info"].toString()))
                            }
                        }

                        else -> {
                            HiroUtils.logWin(this, getString(R.string.txt_unable_to_connect), getString(R.string.txt_info_title)) {
                                this.finishAffinity()
                            }
                        }
                    }
                } catch (ex: Exception) {
                    HiroUtils.logError(this, ex) {
                        this.finishAffinity()
                    }
                }
            },
            {
                HiroUtils.logWin(this, getString(R.string.txt_unable_to_connect), getString(R.string.txt_info_title)) {
                    this.finishAffinity()
                }
            },
            "{\"ret\":\"0\",\"msg\":[{\"id\":1,\"name\":\"usage-1\",\"info\":\"test usage\"},{\"id\":2,\"name\":\"usage-2\",\"info\":\"test usage2\"}]}"
        )
    }

    private fun login() {
        HiroUtils.mainWindowContext?.let {
            val intent = Intent(HiroUtils.mainWindowContext, LoginActivity::class.java)
            ContextCompat.startActivity(it, intent, null)
        }
    }

    private fun tryLogin(username: String?, secret: String?) {
        if (username != null && secret != null) {
            HiroUtils.sendRequest("/user", arrayListOf("action", "username", "token", "device"),
                arrayListOf("0", username, secret, "mobile"),
                {
                    try {
                        val json = JSONObject(it)
                        when (json["ret"]) {
                            "0" -> {
                            }

                            else -> {
                                HiroUtils.parseJsonRet(bi.root, this, it, json["msg"] as String?)
                            }
                        }
                    } catch (ex: Exception) {
                        HiroUtils.logError(this, ex)
                    }
                }, {
                    HiroUtils.logSnackBar(bi.root, getString(R.string.txt_unable_to_connect))
                },
                "{\"ret\":\"0\"}"
            )
        } else
            login()
    }

    fun initalize_Components() {
        //顶部搜索栏
        val mSearchBar = bi.uiIncludeSearchBar
        val mSearchFunction = bi.uiIncludeSearchFunction
        var mSearchEdit = mSearchFunction.uiSearchBar
        mSearchBar.root.setOnClickListener {
            HiroUtils.animateView(
                mSearchBar.root,
                200,
                arrayOf(1f, 0f),
                arrayOf(0f, -200f, 0f, 0f),
                { it.visibility = View.VISIBLE },
                { it.visibility = View.GONE })
            HiroUtils.animateView(
                mSearchFunction.root,
                200,
                arrayOf(0f, 1f),
                arrayOf(200f, 0f, 0f, 0f),
                { it.visibility = View.VISIBLE },
                {
                    it.visibility = View.VISIBLE
                })
            mSearchFunction.uiSearchScan.visibility =
                if (searchMethod == 0) View.VISIBLE else View.GONE
            mSearchEdit.hint = when (searchMethod) {
                0 -> getText(R.string.txt_search_via_shelf)
                1 -> getText(R.string.txt_search_via_name)
                2 -> getText(R.string.txt_search_via_id)
                else -> getText(R.string.txt_search_via_model)
            }
            mSearchFunction.uiSearchMethod.text = when (searchMethod) {
                0 -> getText(R.string.txt_search_method_shelf)
                1 -> getText(R.string.txt_search_method_name)
                2 -> getText(R.string.txt_search_method_id)
                else -> getText(R.string.txt_search_method_model)
            }
            mSearchFunction.uiSearchScan.setOnClickListener {
                startCapture(0x02)
            }
            mSearchEdit.requestFocus()
            HiroUtils.showInputMethod(this@MainActivity, mSearchEdit)
        }

        bi.uiIncludeSearchBar.uiSearchUser.setOnClickListener {
            val intent = Intent()
            intent.setClass(this@MainActivity, MenuActivity::class.java)
            startActivity(intent)
        }
        mSearchFunction.uiSearchMethod.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setCancelable(true)
            builder.setSingleChoiceItems(
                arrayOf(
                    getText(R.string.txt_search_method_shelf).toString(),
                    getText(R.string.txt_search_method_name).toString(),
                    getText(R.string.txt_search_method_id).toString(),
                    getText(R.string.txt_search_method_model).toString()
                ),
                searchMethod
            ) { dialog, which ->
                dialog.dismiss()
                if (searchMethod != which)
                    mSearchEdit.setText("")
                searchMethod = which
                mSearchFunction.uiSearchScan.visibility =
                    if (which == 0) View.VISIBLE else View.GONE
                mSearchEdit.hint = when (which) {
                    0 -> getText(R.string.txt_search_via_shelf)
                    1 -> getText(R.string.txt_search_via_name)
                    2 -> getText(R.string.txt_search_via_id)
                    else -> getText(R.string.txt_search_via_model)
                }
                mSearchFunction.uiSearchMethod.text = when (which) {
                    0 -> getText(R.string.txt_search_method_shelf)
                    1 -> getText(R.string.txt_search_method_name)
                    2 -> getText(R.string.txt_search_method_id)
                    else -> getText(R.string.txt_search_method_model)
                }
            }
            HiroUtils.hideInputMethod(this@MainActivity)
            val dialog = builder.create()
            dialog.setOnDismissListener {
                mSearchEdit.requestFocus()
                HiroUtils.showInputMethod(this@MainActivity, mSearchEdit)
            }
            dialog.show()
        }
        mSearchEdit.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_ENTER) {
                val url = "/search"
                val paraName: List<String> = arrayListOf("username", "token", "device", "method", "keyword")
                val paraValue: List<String> =
                    arrayListOf(
                        HiroUtils.userName ?: "null", HiroUtils.userToken ?: "null", "mobile",
                        (10 + searchMethod).toString(), mSearchEdit.text.toString()
                    )
                HiroUtils.sendRequest(url, paraName, paraValue, {
                    try {
                        val json = JSONObject(it)
                        when (json["ret"]) {
                            "0" -> {
                                val ja = JSONArray(json["msg"].toString())
                                val itemList: MutableList<ShelfAdapter.ShelfItem> = ArrayList()
                                for (i in 0 until ja.length()) {
                                    val jai = ja[i] as JSONObject
                                    val count = (jai["count"] as String).toInt()
                                    val item = ShelfAdapter.ShelfItem(
                                        jai["name"] as String,
                                        count,
                                        jai["model"] as String,
                                        jai["uid"] as String,
                                        HiroUtils.parseShelf(jai),
                                        jai["usage"] as String,
                                        jai["unit"] as String
                                    )
                                    itemList.add(item)
                                }
                                mRecyclerAdapter.setAdapter(itemList)
                            }

                            else -> {
                                HiroUtils.parseJsonRet(bi.root, this, it, json["msg"] as String?)
                            }
                        }

                    } catch (ex: Exception) {
                        HiroUtils.logError(this, ex)
                    }
                }, {
                    HiroUtils.logSnackBar(bi.root, getString(R.string.txt_unable_to_connect))
                },
                    "{\"ret\":\"0\",\"msg\":[{\"desp\":\"No description\",\"uid\":\"test01\",\"unit\":\"s\",\"sshelf\":\"1\",\"usage\":\"1\",\"mshelf\":\"1\",\"count\":\"40\",\"name\":\"For Test Use Only\",\"alias\":\"First Shelf\",\"model\":\"t-1\"}]}"
                )
            }
            keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_ENTER
        }
        mSearchFunction.uiSearchBack.setOnClickListener {
            backToMainSearch()
        }
        //底部入库
        bi.uiImportBtn.setOnClickListener {
            HiroUtils.hideInputMethod(this@MainActivity)
            startActivity(Intent(this@MainActivity, DirectImportActivity::class.java))
        }
    }

    private fun backToMainSearch() {
        HiroUtils.hideInputMethod(this@MainActivity)
        HiroUtils.animateView(
            bi.uiIncludeSearchBar.root,
            200,
            arrayOf(0f, 1f),
            arrayOf(-200f, 0f, 0f, 0f),
            { it.visibility = View.VISIBLE },
            { it.visibility = View.VISIBLE })
        HiroUtils.animateView(
            bi.uiIncludeSearchFunction.root,
            200,
            arrayOf(1f, 0f),
            arrayOf(0f, 200f, 0f, 0f),
            { it.visibility = View.VISIBLE },
            {
                it.visibility = View.GONE
            })
    }

    override fun onBackPressed() {
        if (bi.uiIncludeSearchFunction.root.visibility == View.VISIBLE) {
            backToMainSearch()
        } else
            super.onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 128) {
            if (permissions[0] == Manifest.permission.CAMERA) {
                var flag = true
                grantResults.forEach {
                    if (it != PackageManager.PERMISSION_GRANTED)
                        flag = false
                }
                if (flag) {
                    HiroUtils.logSnackBar(bi.root, getString(R.string.txt_permissions_refused))
                } else {
                    startCapture(0x02)
                }
            }
        }

    }

    private fun startCapture(retCode: Int = 0x01) {
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
                128
            )
        } else {
            // 申请权限之后，调用DefaultView扫码界面。
            ScanUtil.startScan(
                this@MainActivity, retCode, HmsScanAnalyzerOptions.Creator().setHmsScanTypes(
                    HmsScan.ALL_SCAN_TYPE
                ).create()
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) {
            return
        }
        // 从onActivityResult返回data中，用ScanUtil.RESULT作为key值取到HmsScan返回值。
        else if (requestCode == 0x02) {
            when (val obj: Parcelable? = data.getParcelableExtra(ScanUtil.RESULT)) {
                is HmsScan -> {
                    if (!TextUtils.isEmpty(obj.getOriginalValue())) {
                        var ui_search_txt =
                            findViewById<View>(R.id.ui_include_search_function).findViewById<EditText>(
                                R.id.ui_search_bar
                            )
                        Toast.makeText(this, obj.getOriginalValue(), Toast.LENGTH_SHORT).show()
                        ui_search_txt.setText(obj.getOriginalValue())
                    }
                    return
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        HiroUtils.setStatusBarColor(window, resources)
        super.onConfigurationChanged(newConfig)
    }


    private fun initialzeData() {
        val mRecyclerView = bi.uiIncludeMainFunction.uiShelfItemList
        mRecyclerAdapter = ShelfAdapter(
            ArrayList(),
            this, window, 0
        ) {
            val intent = Intent(this, IOActivity::class.java)
            intent.putExtra("maxNum", it.count)
            intent.putExtra("name", it.name)
            intent.putExtra("model", if (it.model == null) getText(R.string.txt_nomodel) else it.model)
            intent.putExtra("uid", if (it.uid == null) getText(R.string.txt_no_id) else it.uid)
            intent.putExtra("shelf.main", it.shelf.main)
            intent.putExtra("shelf.sub", it.shelf.sub)
            intent.putExtra("shelf.alias", it.shelf.alias)
            intent.putExtra("shelf.info", it.shelf.info)
            intent.putExtra("usage.code", it.usage)
            startActivity(intent)
        }
        mRecyclerView.adapter = mRecyclerAdapter
    }

}