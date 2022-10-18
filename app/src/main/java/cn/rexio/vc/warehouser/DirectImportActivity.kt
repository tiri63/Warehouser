package cn.rexio.vc.warehouser

import HiroUtils
import android.app.Activity
import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.core.view.WindowCompat
import cn.rexio.vc.warehouser.databinding.ActivityDirectImportBinding
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import kotlin.concurrent.thread

//Net request OK

class DirectImportActivity : Activity() {
    private lateinit var bi: ActivityDirectImportBinding
    private var searchMethod = 0
    private lateinit var listAdapter: ShelfAdapter

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
                        HiroUtils.userName ?: "null", HiroUtils.userToken ?: "null", "mobile",
                        searchMethod.toString(), bi.ui3DirectImportSearchBarText.text.toString()
                    )
                HiroUtils.sendRequest(
                    "/search", paraName, paraValue,
                    {
                        bi.ui3DirectImportSearchLoading.visibility = View.INVISIBLE
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
                        HiroUtils.logSnackBar(bi.root, getString(R.string.txt_unable_to_connect))
                    },
                    "{\"ret\":\"0\",\"msg\":[{\"uid\":\"test01\",\"unit\":\"s\",\"name\":\"For Test Use Only\",\"model\":\"t-1\"},{\"uid\":\"test02\",\"unit\":\"s\",\"name\":\"For Test Use Only\",\"model\":\"t-2\"}]}\n"
                )
            }
            keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_ENTER
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
}