package cn.rexio.vc.warehouser

import android.app.Activity
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
        setFunctionsList()
    }

    private fun setFunctionsList() {
        var iconList = arrayListOf(R.drawable.ic_profile, R.drawable.ic_info)
        var textList = arrayListOf(R.string.txt_function_profile,R.string.txt_function_info)
        var onClickListenerList = arrayListOf(View.OnClickListener{
            Toast.makeText(applicationContext,getText(textList[0]),LENGTH_LONG).show()
        },View.OnClickListener{
            Toast.makeText(applicationContext,getText(textList[1]),LENGTH_LONG).show()
        })
        val mRecyclerView = findViewById<RecyclerView>(R.id.ui_sub_win_functions_list)
        var mRecyclerAdapter = MenuAdapter(textList, iconList, onClickListenerList)
        mRecyclerView.adapter = mRecyclerAdapter
    }

}