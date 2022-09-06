package cn.rexio.vc.warehouser

import HiroUtils
import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.*


class MainActivity : Activity() {

    private lateinit var ui_scroll_main : ConstraintLayout
    private lateinit var ui_include_viewpager_indicator : ConstraintLayout
    private lateinit var ui_import_tv : TextView
    private lateinit var ui_export_tv : TextView
    private lateinit var ui_indicator_tv : TextView
    private var ui_current = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar.let { it?.hide() }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        initalize_Components()
        setStatusBarColor()
        ui_import_tv.setOnClickListener {
            set_viewpager_indicator(ui_import_tv.marginLeft.toFloat())
        }
        ui_export_tv.setOnClickListener {
            set_viewpager_indicator(ui_export_tv.marginLeft.toFloat())
        }





    }

    fun initalize_Components()
    {
        ui_scroll_main = findViewById(R.id.ui_scroll_main)
        ui_include_viewpager_indicator = findViewById(R.id.ui_include_viewpager_indicator)
        ui_import_tv = ui_include_viewpager_indicator.findViewById(R.id.ui_import_tv)
        ui_export_tv = ui_include_viewpager_indicator.findViewById(R.id.ui_export_tv)
        ui_indicator_tv = ui_include_viewpager_indicator.findViewById(R.id.ui_indicator_tv)
        var ui_profile_button : ImageView = findViewById(R.id.ui_search_user)
        ui_profile_button.setOnClickListener({
            val intent = Intent()
            intent.setClass(this@MainActivity, MenuActivity::class.java)
            startActivity(intent)
        })
    }

    fun set_viewpager_indicator(target : Float)
    {
        val currentX: Float= ui_indicator_tv.translationX
        val animation = ObjectAnimator.ofFloat(ui_indicator_tv,"translationX",currentX,target)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        this.setStatusBarColor()
        super.onConfigurationChanged(newConfig)
    }

    private fun setStatusBarColor()
    {
        HiroUtils.setStatusBarColor(window,resources)
    }

}