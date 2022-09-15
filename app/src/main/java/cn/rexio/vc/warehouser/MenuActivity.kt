package cn.rexio.vc.warehouser

import HiroUtils.Factory.setStatusBarColor
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.recyclerview.widget.RecyclerView

class MenuActivity : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar.let { it?.hide() }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_layout)
        setStatusBarColor(window,resources)
        setFunctionsList()
    }

    private fun setFunctionsList() {
        var iconList = arrayListOf(R.drawable.ic_profile, R.drawable.ic_info)
        var textList = arrayListOf(R.string.txt_function_profile,R.string.txt_function_info)
        var onClickListenerList = arrayListOf(View.OnClickListener{
            val intent = Intent()
            intent.setClass(this@MenuActivity, ProfileActivity::class.java)
            startActivity(intent)
        },View.OnClickListener{
            Toast.makeText(applicationContext,getText(textList[1]),LENGTH_LONG).show()
        })
        val mRecyclerView = findViewById<RecyclerView>(R.id.ui_search_result)
        var mRecyclerAdapter = MenuAdapter(textList, iconList, onClickListenerList)
        mRecyclerView.adapter = mRecyclerAdapter
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        setStatusBarColor(window,resources)
        super.onConfigurationChanged(newConfig)
    }

}