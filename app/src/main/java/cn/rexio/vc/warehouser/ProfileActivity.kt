package cn.rexio.vc.warehouser

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import cn.rexio.vc.warehouser.databinding.ActivityProfileBinding

class ProfileActivity : Activity() {
    private lateinit var bi: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar.let { it?.hide() }
        super.onCreate(savedInstanceState)
        bi = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(bi.root)
        HiroUtils.setPopWinStatusBarColor(window,resources)
        HiroUtils.animateView(bi.ui3ProfileTitle, 300, arrayOf(0f, 1f), arrayOf(200f, 0f, 0f, 0f), {}, {})
        HiroUtils.animateView(bi.ui3ProfileRoot, 300, arrayOf(0f, 1f), arrayOf(0f, 0f, 600f, 0f), {}, {})
    }

    override fun finish() {
        HiroUtils.animateView(bi.ui3ProfileRoot, 150, arrayOf(1f, 0f), arrayOf(0f, 0f, 0f, 600f), {}, {})
        HiroUtils.animateView(bi.ui3ProfileTitle, 150, arrayOf(1f, 0f), arrayOf(0f, -200f, 0f, 0f), {}, { super.finish() })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        HiroUtils.setPopWinStatusBarColor(window, resources)
        super.onConfigurationChanged(newConfig)
    }
}