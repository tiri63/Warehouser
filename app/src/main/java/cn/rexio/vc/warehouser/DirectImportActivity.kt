package cn.rexio.vc.warehouser

import HiroUtils
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.view.WindowCompat
import cn.rexio.vc.warehouser.databinding.ActivityDirectImportBinding
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class DirectImportActivity : Activity() {
    private lateinit var bi: ActivityDirectImportBinding
    private var searchMethod = 0//0..2
    private lateinit var mRecyclerAdapter : ShelfAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        bi = ActivityDirectImportBinding.inflate(layoutInflater)
        setContentView(bi.root)
        HiroUtils.setPopWinStatusBarColor(window, resources)
        syncSearchMethod()
        YoYo.with(Techniques.FadeIn).duration(200).interpolate(DecelerateInterpolator())
            .playOn(bi.ui3DirectImportRoot).run { }
        initialize()

    }

    private fun initialize(){
        mRecyclerAdapter = ShelfAdapter(ArrayList(),this,window,1)
        bi.ui3DirectImportList.adapter = mRecyclerAdapter
        initializeOnClickListener()
    }

    private fun initializeOnClickListener(){
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

        bi.ui3DirectImportSearchBar.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_ENTER) {
                val url = "${HiroUtils.baseUrl}/search"
                val paraName: List<String> = arrayListOf("username", "token", "device","keyword","method")
                val paraValue: List<String> =
                    arrayListOf(HiroUtils.userName,HiroUtils.userToken,"mobile",
                        bi.ui3DirectImportSearchBarText.text.toString(),searchMethod.toString())
                HiroUtils.sendRequest(url, paraName, paraValue, {
                    try{
                        val json = JSONObject(it)
                        if (json["ret"] == "1") {
                            //解析数据
                            val ja = JSONArray(json["msg"])
                            val itemList: MutableList<ShelfAdapter.ShelfItem> = ArrayList()
                            for (i in 0..ja.length()) {
                                val jai = ja[i] as JSONObject
                                val item = ShelfAdapter.ShelfItem(
                                    jai["name"] as String,
                                    0,
                                    jai["model"] as String,
                                    jai["uid"] as String,
                                    "shelf",
                                    "usage",
                                    jai["unit"] as String
                                )
                                itemList.add(item)
                            }
                            mRecyclerAdapter.setAdapter(itemList)
                        } else if (json["ret"] == "2") {
                            //认证过期
                            HiroUtils.logInfo(bi.root,R.string.txt_token_invalid)
                            val intent = Intent()
                            intent.setClass(this,LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            HiroUtils.logInfo(bi.root,"${json["ret"]}:${json["msg"]}")
                        }
                    }
                    catch (ex:Exception)
                    {
                        HiroUtils.logInfo(bi.root,ex)
                    }

                }, {
                    HiroUtils.logInfo(bi.root,R.string.txt_unable_to_connect)
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

    private fun changeView(direction: Boolean) {
        if (direction) {
            HiroUtils.hideInputMethod(this@DirectImportActivity)
            HiroUtils.viewAnimation(bi.ui3DirectImportMain, Techniques.FadeOutLeft, 200, {
                it.visibility = View.VISIBLE
            }, {
                it.visibility = View.INVISIBLE
            })
            HiroUtils.viewAnimation(bi.ui3DirectImportSearchLayout, Techniques.FadeInRight, 200, {
                it.visibility = View.VISIBLE
            }, {
                it.visibility = View.VISIBLE
                bi.ui3DirectImportSearchBarText.requestFocus()
                HiroUtils.showInputMethod(this@DirectImportActivity, bi.ui3DirectImportSearchBarText)
            })
        } else {
            HiroUtils.hideInputMethod(this@DirectImportActivity)
            HiroUtils.viewAnimation(bi.ui3DirectImportMain, Techniques.FadeInLeft, 200, {
                it.visibility = View.VISIBLE
            }, {
                it.visibility = View.VISIBLE
            })
            HiroUtils.viewAnimation(bi.ui3DirectImportSearchLayout, Techniques.FadeOutRight, 200, {
                it.visibility = View.VISIBLE
            }, {
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