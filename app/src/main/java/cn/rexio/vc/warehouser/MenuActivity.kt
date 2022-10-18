package cn.rexio.vc.warehouser

import HiroUtils
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import cn.rexio.vc.warehouser.databinding.MenuLayoutBinding

class MenuActivity : Activity() {
    private lateinit var bi: MenuLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        actionBar.let { it?.hide() }
        super.onCreate(savedInstanceState)
        bi = MenuLayoutBinding.inflate(layoutInflater)
        setContentView(bi.root)
        HiroUtils.setPopWinStatusBarColor(window, resources)
        setFunctionsList()
        HiroUtils.animateView(bi.ui3MenuRoot, 300, arrayOf(0f, 1f), arrayOf(0f, 0f, 400f, 0f), {}, {})
        bi.ui3MenuUsername.text = getString(R.string.txt_profile_hi, HiroUtils.userNickName)
    }

    override fun finish() {
        HiroUtils.animateView(bi.ui3MenuRoot, 150, arrayOf(1f, 0f), arrayOf(0f, 0f, 0f, -400f), {}, { super.finish() })
    }

    private fun setFunctionsList() {
        var iconList = arrayListOf(R.drawable.ic_profile, R.drawable.ic_info)
        var textList = arrayListOf(R.string.txt_profile_update_pwd, R.string.txt_function_info)
        var onClickListenerList = arrayListOf(View.OnClickListener {
            HiroUtils.mainWindowContext?.let {
                val intent = Intent(HiroUtils.mainWindowContext, ProfileActivity::class.java)
                ContextCompat.startActivity(it, intent, null)
            }
            finish()
        }, View.OnClickListener {
            HiroUtils.mainWindowContext?.let {
                val intent = Intent(HiroUtils.mainWindowContext, AboutActivity::class.java)
                ContextCompat.startActivity(it, intent, null)
            }
            finish()
        })
        val mRecyclerAdapter = MenuAdapter(textList, iconList, onClickListenerList)
        bi.ui3MenuFun.adapter = mRecyclerAdapter
        bi.ui3MenuLogout.setOnClickListener {
            HiroUtils.logOut()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        HiroUtils.setPopWinStatusBarColor(window, resources)
        super.onConfigurationChanged(newConfig)
    }


}