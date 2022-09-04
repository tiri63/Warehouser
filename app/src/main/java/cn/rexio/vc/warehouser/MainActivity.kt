package cn.rexio.vc.warehouser

import HiroUtils
import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar.let { it?.hide() }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.main_layout)
        setStatusBarColor()
        var ui_scroll_main = findViewById<ConstraintLayout>(R.id.ui_scroll_main)
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