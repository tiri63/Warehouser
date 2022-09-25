package cn.rexio.vc.warehouser

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import cn.rexio.vc.warehouser.databinding.MenuLayoutBinding

class MenuActivity : Activity() {
    private lateinit var bi: MenuLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar.let { it?.hide() }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        bi = MenuLayoutBinding.inflate(layoutInflater)
        setContentView(bi.root)
        HiroUtils.setPopWinStatusBarColor(window, resources)
        setLogo()
        setFunctionsList()
    }

    private fun setLogo() {
        val isLight =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO
        if (isLight)
            bi.ui3MenuLogo.setImageResource(R.drawable.ic_weichai_logo)
        else
            bi.ui3MenuLogo.setImageResource(R.drawable.ic_weichai_logo_night)

    }

    private fun setFunctionsList() {
        var iconList = arrayListOf(R.drawable.ic_profile, R.drawable.ic_info)
        var textList = arrayListOf(R.string.txt_function_profile, R.string.txt_function_info)
        var onClickListenerList = arrayListOf(View.OnClickListener {
            val intent = Intent()
            intent.setClass(this@MenuActivity, ProfileActivity::class.java)
            startActivity(intent)
        }, View.OnClickListener {
            val intent = Intent()
            intent.setClass(this@MenuActivity, AboutActivity::class.java)
            startActivity(intent)
            this.finish()
        })
        val mRecyclerAdapter = MenuAdapter(textList, iconList, onClickListenerList)
        bi.uiSearchResult.adapter = mRecyclerAdapter
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        HiroUtils.setPopWinStatusBarColor(window, resources)
        setLogo()
        super.onConfigurationChanged(newConfig)
    }


}