package cn.rexio.vc.warehouser

import HiroUtils.Factory.setStatusBarColor
import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText

class SearchActivity : Activity() {
    private lateinit var ui_search_bar : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar.let { it?.hide() }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setStatusBarColor(window,resources)

        var ui_search = findViewById<View>(R.id.ui_include_search_bar)
        ui_search_bar = ui_search.findViewById(R.id.ui_search_bar)
        ui_search_bar.setOnClickListener{

        }
        ui_search_bar.requestFocus()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        setStatusBarColor(window,resources)
        super.onConfigurationChanged(newConfig)
    }
}