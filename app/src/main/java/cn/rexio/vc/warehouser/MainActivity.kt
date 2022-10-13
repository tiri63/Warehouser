package cn.rexio.vc.warehouser

import HiroUtils
import SharedPref
import android.animation.Animator
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cn.rexio.vc.warehouser.databinding.MainLayoutBinding
import com.google.android.material.snackbar.Snackbar
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
        fakeData()
    }

    fun loadSettings() {
        username = SharedPref(this, "username", "null").getValue(this, this::username)
        secret = SharedPref(this, "secret", "null").getValue(this, this::secret)
        if (username.equals("null") || secret.equals("null")) {
            login()
        } else
            tryLogin(username, secret)
    }

    private fun login() {
        HiroUtils.mainWindowContext?.let {
            val intent = Intent(HiroUtils.mainWindowContext, LoginActivity::class.java)
            ContextCompat.startActivity(it, intent, null)
        }
    }

    private fun tryLogin(username: String?, secret: String?) {
        if (username != null && secret != null) {
            HiroUtils.sendRequest("baseURL/login", arrayListOf("username", "secret"),
                arrayListOf(username, secret),
                {
                    if (it == "success") {
                        return@sendRequest
                    } else {
                        Snackbar.make(bi.root, R.string.txt_login_failed, Snackbar.LENGTH_SHORT).show()
                    }
                }, {
                    Snackbar.make(bi.root, R.string.txt_unable_to_connect, Snackbar.LENGTH_SHORT).show()
                },
                "success"
            )
        }
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
        mSearchEdit.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_ENTER) {
                val url: String = "baseURL/search"
                val paraName: List<String> = arrayListOf("category", "method", "word")
                val paraValue: List<String> =
                    arrayListOf("0", searchMethod.toString(), mSearchEdit.text.toString())
                HiroUtils.sendRequest(url, paraName, paraValue, {
                    val json = JSONObject(it)
                    if (json["status"] == "1") {
                        //解析数据
                        val ja = JSONArray(json["data"])
                        val itemList: MutableList<ShelfAdapter.ShelfItem> = ArrayList()
                        for (i in 0..ja.length()) {
                            val jai = ja[i] as JSONObject
                            val count = (jai["count"] as String).toInt()
                            val item = ShelfAdapter.ShelfItem(
                                jai["name"] as String,
                                count,
                                jai["model"] as String,
                                jai["uid"] as String,
                                jai["shelf"] as String,
                                jai["usage"] as String
                            )
                            itemList.add(item)
                        }
                        mRecyclerAdapter.setAdapter(itemList)
                    } else if (json["status"] == "2") {
                        //认证过期
                    } else {
                        Toast.makeText(
                            this,
                            getText(R.string.txt_unable_to_connect),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, {
                    Toast.makeText(
                        this,
                        getText(R.string.txt_unable_to_connect),
                        Toast.LENGTH_SHORT
                    ).show()
                },
                    "{\n" +
                            "    \"status\": \"1\",\n" +
                            "    \"data\": [\n" +
                            "        {\n" +
                            "            \"name\": \"Name\",\n" +
                            "            \"model\": \"X1\",\n" +
                            "            \"count\": \"12\",\n" +
                            "            \"shelf\": \"3-2\",\n" +
                            "            \"use\": \"1-1\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"name\": \"Name2\",\n" +
                            "            \"model\": \"S-2\",\n" +
                            "            \"count\": \"10\",\n" +
                            "            \"shelf\": \"3-3\",\n" +
                            "            \"use\": \"2-1\"\n" +
                            "        }\n" +
                            "    ]\n" +
                            "}"
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

    private fun startCapture(retCode: Int = 0x01) {
        // 申请权限之后，调用DefaultView扫码界面。
        ScanUtil.startScan(
            this@MainActivity, retCode, HmsScanAnalyzerOptions.Creator().setHmsScanTypes(
                HmsScan.ALL_SCAN_TYPE
            ).create()
        )
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


    private fun fakeData() {
        val mRecyclerView =
            findViewById<View>(R.id.ui_include_main_function).findViewById<RecyclerView>(R.id.ui_shelf_item_list)
        mRecyclerAdapter = ShelfAdapter(
            arrayListOf(
                ShelfAdapter.ShelfItem("螺丝1", 10, "150mm-X", "1000001", "仓库2-货架1-第1层", "维修"),
                ShelfAdapter.ShelfItem("螺丝2", 20, "150mm-Y", "7644758", "仓库1-货架1-第1层", "存储"),
                ShelfAdapter.ShelfItem("螺丝3", 13, "120mm-X", "0678742", "仓库2-货架3-第1层", "暂存"),
                ShelfAdapter.ShelfItem("螺丝4", 10, "170mm-X", "7352244", "仓库2-货架1-第1层", "无用"),
                ShelfAdapter.ShelfItem("螺丝5", 50, "150mm-Z", "4893452", "仓库4-货架1-第1层", "维修"),
                ShelfAdapter.ShelfItem("螺丝6", 70, "110mm-X", "xca2345", "仓库2-货架1-第1层", "熔炼"),
                ShelfAdapter.ShelfItem("螺丝7", 19, "150mm-B", null, "仓库5-货架1-第1层", "维修"),
                ShelfAdapter.ShelfItem("螺丝8", 64, "100mm-X", "1000001", "仓库2-货架1-第1层", "维修"),
                ShelfAdapter.ShelfItem("螺丝9", 75, "150mm-X", "65y2s1s", "仓库2-货架1-第1层", "外部"),
                ShelfAdapter.ShelfItem("螺丝X", 10, "150mm-X", "1000001", "仓库6-货架1-第1层", "维修"),
                ShelfAdapter.ShelfItem("螺丝A", 31, null, "1000001", "仓库2-货架1-第1层", "维修"),
                ShelfAdapter.ShelfItem("螺丝B", 63, "150mm-X", "1000001", "仓库2-货架1-第1层", "维修"),
                ShelfAdapter.ShelfItem("螺丝C", 67, "150mm-X", "1000001", "仓库1-货架1-第1层", "维修"),
                ShelfAdapter.ShelfItem("螺丝D", 10, "150mm-X", "1000001", "仓库2-货架1-第1层", "维修"),
                ShelfAdapter.ShelfItem("螺丝E", 35, "150mm-X", "1000001", "仓库2-货架1-第1层", null),
                ShelfAdapter.ShelfItem("螺丝F", 99, "150mm-X", "1000001", "仓库A-货架1-第1层", "维修"),
                ShelfAdapter.ShelfItem("螺丝G", 54, "150mm-X", "1000001", "仓库2-货架1-第1层", "维修"),
                ShelfAdapter.ShelfItem("螺丝H", 72, "150mm-X", "1000001", "仓库2-货架1-第1层", "维修")
            ),
            this, window
        )
        mRecyclerView.adapter = mRecyclerAdapter
    }

}