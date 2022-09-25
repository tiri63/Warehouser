package cn.rexio.vc.warehouser

import android.app.Activity
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.WindowCompat
import cn.rexio.vc.warehouser.databinding.ActivityDirectImportBinding
import cn.rexio.vc.warehouser.databinding.ActivityLogBinding

class LogActivity : Activity() {
    private lateinit var bi: ActivityLogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        bi = ActivityLogBinding.inflate(layoutInflater)
        setContentView(bi.root)
        HiroUtils.setPopWinStatusBarColor(window, resources)
        bi.ui3LogText.setText(intent.extras?.getString("log"))
        intent.extras?.getString("title")?.let {
            bi.ui3LogTitle.text = it
        }
        bi.ui3LogBtn.setOnClickListener{
            this.finish()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        HiroUtils.setPopWinStatusBarColor(window, resources)
        super.onConfigurationChanged(newConfig)
    }
}