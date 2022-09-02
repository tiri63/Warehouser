package cn.rexio.vc.warehouser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        supportActionBar.let { it?.hide() }
        var ui_scroll_main = findViewById<ConstraintLayout>(R.id.ui_scroll_main)
    }
}