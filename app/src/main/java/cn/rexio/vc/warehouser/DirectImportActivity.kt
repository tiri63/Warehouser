package cn.rexio.vc.warehouser

import HiroUtils
import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.view.WindowCompat
import cn.rexio.vc.warehouser.databinding.ActivityDirectImportBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import org.json.JSONObject
import kotlin.concurrent.thread

class DirectImportActivity : Activity() {
    private lateinit var bi: ActivityDirectImportBinding
    private var searchMethod = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        bi = ActivityDirectImportBinding.inflate(layoutInflater)
        setContentView(bi.root)
        HiroUtils.setPopWinStatusBarColor(window, resources)
        syncSearchMethod()
        HiroUtils.animateView(bi.ui3DirectImportRoot, 150, arrayOf(0f, 1f), arrayOf(0f, 0f, 600f, 0f), {}, {})
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

        bi.ui3DirectImportSearchBar.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_ENTER) {
                val url: String = "baseURL/search"
                val paraName: List<String> = arrayListOf("category", "method", "word")
                val paraValue: List<String> =
                    arrayListOf("1", searchMethod.toString(), bi.ui3DirectImportSearchBarText.text.toString())
                HiroUtils.sendRequest(url, paraName, paraValue, {
                    val json = JSONObject(it)
                    if (json["status"] == "1") {
                        //解析数据
                        val ja = JSONArray(json["data"])
                        val itemList: MutableList<ShelfAdapter.ShelfItem> = ArrayList()
                        for (i in 0..ja.length()) {
                            val jai = ja[i] as JSONObject
                            val item = ShelfAdapter.ShelfItem(
                                jai["name"] as String,
                                0,
                                jai["model"] as String,
                                jai["uid"] as String,
                                jai["shelf"] as String,
                                jai["usage"] as String
                            )
                            itemList.add(item)
                        }
                        //mRecyclerAdapter.setAdapter(itemList)
                    } else if (json["status"] == "2") {
                        //认证过期
                    } else {
                        Snackbar.make(bi.root, R.string.txt_unable_to_connect, Snackbar.LENGTH_SHORT).show()
                    }
                }, {
                    Snackbar.make(bi.root, R.string.txt_unable_to_connect, Snackbar.LENGTH_SHORT).show()
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
    }

    override fun onBackPressed() {
        if (bi.ui3DirectImportMain.visibility != View.VISIBLE)
            changeView(false)
        else
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