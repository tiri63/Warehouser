package cn.rexio.vc.warehouser

import HiroUtils
import android.app.Activity
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.WindowCompat
import cn.rexio.vc.warehouser.databinding.ActivityDirectImportBinding
import cn.rexio.vc.warehouser.databinding.ActivityLogBinding
import kotlin.concurrent.thread

class LogActivity : Activity() {
    private lateinit var bi: ActivityLogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        bi = ActivityLogBinding.inflate(layoutInflater)
        setContentView(bi.root)
        HiroUtils.animateView(bi.ui3LogRoot, 300, arrayOf(0f, 1f), arrayOf(0f, 0f, 600f, 0f), {}, {})
        HiroUtils.setPopWinStatusBarColor(window, resources)
        bi.ui3LogText.setText(intent.extras?.getString("log"))
        intent.extras?.getString("title")?.let {
            bi.ui3LogTitle.text = it
        }
        bi.ui3LogBtn.setOnClickListener {
            this.finish()
        }
    }

    override fun finish() {
        HiroUtils.animateView(bi.ui3LogRoot, 150, arrayOf(1f, 0f), arrayOf(0f, 0f, 0f, 600f), {}, {super.finish()})
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        HiroUtils.setPopWinStatusBarColor(window, resources)
        super.onConfigurationChanged(newConfig)
    }
}