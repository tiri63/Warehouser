package cn.rexio.vc.warehouser

import HiroUtils
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import cn.rexio.vc.warehouser.databinding.ActivityDirectImportBinding
import com.google.android.material.chip.Chip
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.concurrent.thread


//Net request OK

class DirectImportActivity : Activity() {
    private lateinit var bi: ActivityDirectImportBinding
    private var searchMethod = 0
    private lateinit var listAdapter: ShelfAdapter
    val maxNum = 999
    private var usage: ArrayList<String> = ArrayList()
    private var shelf = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        bi = ActivityDirectImportBinding.inflate(layoutInflater)
        setContentView(bi.root)
        HiroUtils.setPopWinStatusBarColor(window, resources)
        syncSearchMethod()
        HiroUtils.animateView(bi.ui3DirectImportRoot, 150, arrayOf(0f, 1f), arrayOf(0f, 0f, 600f, 0f), {}, {})

        initializeData()
        initializeListener()
        syncUsage()
    }

    private fun initializeData() {
        listAdapter = ShelfAdapter(ArrayList(), this, window, 1) {
            bi.ui3DirectImportTitle.text = it.name
            bi.ui3DirectImportModel.text = it.model
            bi.ui3DirectImportUid.text = it.uid
            changeView(false)
        }
        bi.ui3DirectImportList.adapter = listAdapter
    }

    private fun initializeListener() {
        bi.ui3DirectImportSearch.setOnClickListener {
            changeView(true)
        }

        bi.ui3DirectImportBack.setOnClickListener {
            if (bi.ui3DirectImportMain.visibility != View.VISIBLE)
                changeView(false)
            else
                this.finish()
        }


        bi.ui3DirectImportSearchMethod.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setCancelable(true)
            builder.setSingleChoiceItems(
                arrayOf(
                    getText(R.string.txt_search_method_name).toString(),
                    getText(R.string.txt_search_method_id).toString(),
                    getText(R.string.txt_search_method_model).toString()
                ),
                searchMethod
            ) { dialog, which ->
                dialog.dismiss()
                if (searchMethod != which)
                    bi.ui3DirectImportSearchBarText.setText("")
                searchMethod = which
                syncSearchMethod()
            }
            HiroUtils.hideInputMethod(this@DirectImportActivity)
            val dialog = builder.create()
            dialog.setOnDismissListener {
                bi.ui3DirectImportSearchBar.requestFocus()
                HiroUtils.showInputMethod(this@DirectImportActivity, bi.ui3DirectImportSearchBarText)
            }
            dialog.show()
        }

        bi.ui3DirectImportSearchBarText.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_ENTER) {
                bi.ui3DirectImportSearchLoading.visibility = View.VISIBLE
                val paraName: List<String> = arrayListOf("username", "token", "device", "method", "keyword")
                val paraValue: List<String> =
                    arrayListOf(
                        HiroUtils.userName ?: "null",
                        HiroUtils.userToken ?: "null",
                        "mobile",
                        searchMethod.toString(),
                        URLEncoder.encode(bi.ui3DirectImportSearchBarText.text.toString(), "UTF-8")
                    )
                HiroUtils.sendRequest(
                    "/search", paraName, paraValue,
                    {
                        bi.ui3DirectImportSearchLoading.visibility = View.INVISIBLE
                        bi.ui3DirectImportList.visibility = View.VISIBLE
                        try {
                            val json = JSONObject(it)
                            when (json["ret"]) {
                                "0" -> {
                                    val ja = JSONArray(json["msg"].toString())
                                    val itemList = ArrayList<ShelfAdapter.ShelfItem>()
                                    for (i in 0 until ja.length()) {
                                        val jai = ja[i] as JSONObject
                                        val item = ShelfAdapter.ShelfItem(
                                            jai["name"] as String,
                                            0,
                                            jai["model"] as String,
                                            jai["uid"] as String,
                                            HiroUtils.parseShelf(json),
                                            null,
                                            jai["unit"] as String,
                                            null
                                        )
                                        itemList.add(item)
                                    }
                                    listAdapter.setAdapter(itemList)
                                }

                                else -> {
                                    HiroUtils.parseJsonRet(bi.root, this, it, json["msg"] as String?)
                                }
                            }
                        } catch (ex: Exception) {
                            HiroUtils.logError(this, ex)
                        }
                    },
                    {
                        bi.ui3DirectImportSearchLoading.visibility = View.INVISIBLE
                        bi.ui3DirectImportList.visibility = View.VISIBLE
                        HiroUtils.logSnackBar(bi.root, getString(R.string.txt_unable_to_connect))
                    },
                    this
                )
            }
            keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_ENTER
        }
        bi.ui3DirectImportShelf.setOnClickListener {
            startCapture(0x02)
        }
        bi.ui3DirectImportMin.setOnClickListener {
            bi.ui3DirectImportCount.setText("0")
        }
        bi.ui3DirectImportMax.setOnClickListener {
            bi.ui3DirectImportCount.setText(maxNum.toString())
        }
        bi.ui3DirectImportMinus.setOnClickListener {
            try {
                var current = bi.ui3DirectImportCount.text.toString().toInt() - 1
                if (current < 0)
                    current = 0
                bi.ui3DirectImportCount.setText(current.toString())
            } catch (_: Exception) {
                bi.ui3DirectImportCount.setText("0")
            }
        }
        bi.ui3DirectImportPlus.setOnClickListener {
            try {
                var current = bi.ui3DirectImportCount.text.toString().toInt() + 1
                if (current > maxNum)
                    current = maxNum
                bi.ui3DirectImportCount.setText(current.toString())
            } catch (_: Exception) {
                bi.ui3DirectImportCount.setText(maxNum.toString())
            }
        }
        bi.ui3DirectImportBtn.setOnClickListener {
            if (shelf == "") {
                return@setOnClickListener
            }
            val para = arrayListOf("username", "token", "device", "action", "shelf", "uid", "count", "function")
            //0 for import and 1 for export, shelf is sfid
            var usagetext = ""
            usage.forEach {
                if (usagetext == "")
                    usagetext = it
                else
                    usagetext = "$usagetext,$it"
            }
            val value =
                arrayListOf(
                    HiroUtils.userName ?: "null", HiroUtils.userToken ?: "null", "mobile", "0",
                    shelf,
                    bi.ui3DirectImportUid.text.toString(), bi.ui3DirectImportCount.text.toString(), usagetext
                )
            Log.i("usage",usagetext)
            bi.ui3DirectImportBtnLoading.visibility = View.VISIBLE
            bi.ui3DirectImportBtn.isEnabled = false
            HiroUtils.sendRequest(
                "/io", para, value,
                {
                    bi.ui3DirectImportBtnLoading.visibility = View.INVISIBLE
                    try {
                        Log.i("http", it)
                        val json = JSONObject(it)
                        when (json["ret"]) {
                            "0" -> {
                                HiroUtils.logSnackBar(bi.root, getString(R.string.txt_successed))
                                this.finish()
                            }

                            else -> {
                                bi.ui3DirectImportBtn.isEnabled = true
                                HiroUtils.parseJsonRet(bi.root, this, it, json["msg"] as String?)
                            }
                        }
                    } catch (ex: Exception) {
                        bi.ui3DirectImportBtn.isEnabled = true
                        HiroUtils.logError(this, ex)
                    }
                },
                {
                    bi.ui3DirectImportBtnLoading.visibility = View.INVISIBLE
                    bi.ui3DirectImportBtn.isEnabled = true
                    HiroUtils.logSnackBar(bi.root, getString(R.string.txt_unable_to_connect))
                },
                this
            )
        }
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
                this@DirectImportActivity, retCode, HmsScanAnalyzerOptions.Creator().setHmsScanTypes(
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
                        shelf = obj.getOriginalValue()
                        if (shelf.startsWith(HiroUtils.baseURL + "/qr?i="))
                            shelf =
                                shelf.removePrefix(HiroUtils.baseURL + "/qr?i=")
                        else
                            shelf = ""
                        bi.ui3DirectImportShelf.text = if(shelf == "") getString(R.string.txt_dialog_click_to_set_shelf) else shelf
                    }
                    return
                }
            }
        }
    }

    override fun onBackPressed() {
        if (bi.ui3DirectImportMain.visibility != View.VISIBLE) {
            if (bi.ui3DirectImportSearchLoading.visibility != View.VISIBLE)
                changeView(false)
        } else
            this.finish()
    }

    override fun finish() {
        HiroUtils.animateView(bi.ui3DirectImportRoot, 200, arrayOf(1f, 0f), arrayOf(0f, 0f, 0f, 600f), {}, {})
        thread {
            Thread.sleep(200)
            super.finish()
        }
    }

    private fun changeView(direction: Boolean) {
        if (direction) {
            HiroUtils.hideInputMethod(this@DirectImportActivity)
            HiroUtils.animateView(
                bi.ui3DirectImportMain,
                200,
                arrayOf(1f, 0f),
                arrayOf(0f, -200f, 0f, 0f),
                { it.visibility = View.VISIBLE },
                { it.visibility = View.INVISIBLE })
            HiroUtils.animateView(
                bi.ui3DirectImportSearchLayout,
                200,
                arrayOf(0f, 1f),
                arrayOf(200f, 0f, 0f, 0f),
                { it.visibility = View.VISIBLE },
                {
                    it.visibility = View.VISIBLE
                    bi.ui3DirectImportSearchBarText.requestFocus()
                    HiroUtils.showInputMethod(this@DirectImportActivity, bi.ui3DirectImportSearchBarText)
                })

        } else {
            HiroUtils.hideInputMethod(this@DirectImportActivity)
            HiroUtils.animateView(
                bi.ui3DirectImportMain,
                200,
                arrayOf(0f, 1f),
                arrayOf(-200f, 0f, 0f, 0f),
                { it.visibility = View.VISIBLE },
                { it.visibility = View.VISIBLE })
            HiroUtils.animateView(
                bi.ui3DirectImportSearchLayout,
                200,
                arrayOf(1f, 0f),
                arrayOf(0f, 200f, 0f, 0f),
                { it.visibility = View.VISIBLE },
                {
                    it.visibility = View.INVISIBLE
                })
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        HiroUtils.setPopWinStatusBarColor(window, resources)
        super.onConfigurationChanged(newConfig)
    }

    private fun syncSearchMethod() {
        bi.ui3DirectImportSearchMethod.text = when (searchMethod) {
            0 -> getText(R.string.txt_search_method_name)
            1 -> getText(R.string.txt_search_method_id)
            else -> getText(R.string.txt_search_method_model)
        }
        bi.ui3DirectImportSearchBar.hint = when (searchMethod) {
            0 -> getText(R.string.txt_search_via_name)
            1 -> getText(R.string.txt_search_via_id)
            else -> getText(R.string.txt_search_via_model)
        }
    }

    private fun syncUsage() {
        HiroUtils.usageArray.forEach {
            if(!it.hide)
            {
                val chip = Chip(this)
                chip.text = it.alias
                chip.tag = it.code.toString()
                chip.setChipBackgroundColorResource(R.color.BackgroundColor)
                chip.setRippleColorResource(R.color.AccentColorDim)
                chip.setTextColor(getColor(R.color.FontColor))
                chip.setChipStrokeColorResource(R.color.FontColor)
                chip.setOnClickListener {
                    chip.isCheckable = !chip.isCheckable
                    if (chip.isCheckable) {
                        chip.setChipBackgroundColorResource(R.color.AccentColor)
                        chip.setTextColor(getColor(R.color.BackgroundColor))
                        if (!usage.contains(chip.tag as String)) {
                            usage.add(chip.tag as String)
                        }
                    } else {
                        chip.setChipBackgroundColorResource(R.color.BackgroundColor)
                        chip.setTextColor(getColor(R.color.FontColor))
                        if (usage.contains(chip.tag as String)) {
                            usage.remove(chip.tag as String)
                        }
                    }
                }
                bi.ui3DirectImportFor.addView(chip)
            }
        }
    }
}