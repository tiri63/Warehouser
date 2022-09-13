package cn.rexio.vc.warehouser

import HiroUtils
import SharedPref
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.*


class MainActivity : Activity() {
    private var username: String? = null
    private var secret: String? = null
    private lateinit var ui_scroll_main: ConstraintLayout
    private lateinit var ui_include_viewpager_indicator: ConstraintLayout
    private lateinit var ui_import_tv: TextView
    private lateinit var ui_export_tv: TextView
    private lateinit var ui_indicator_tv: TextView
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
        loadSettings()
    }

    fun loadSettings() {
        username = SharedPref(this, "username", "null").getValue(this, this::username)
        secret = SharedPref(this, "secret", "null").getValue(this, this::secret)
        if (username.equals("null") || secret.equals("null"))
            login()
        else
            tryLogin(username, secret)

    }

    fun login() {
        val alertDialog =
            AlertDialog.Builder(this).setView(R.layout.dialog_login)
        alertDialog.show()
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        val view = layoutInflater.inflate(R.layout.dialog_login, null, false)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        view.findViewById<TextView>(R.id.dialog_login).setOnClickListener {
            Toast.makeText(
                this,
                view.findViewById<EditText>(R.id.login_name).text.toString() + " :: " +
                        view.findViewById<EditText>(R.id.login_password).text.toString(),
                Toast.LENGTH_SHORT
            ).show()
            SharedPref(this, "username", "null").setValue(
                this,
                this::username,
                view.findViewById<EditText>(R.id.login_name).text.toString()
            )
            SharedPref(this, "secret", "null").setValue(
                this,
                this::secret,
                view.findViewById<EditText>(R.id.login_password).text.toString()
            )
        }
    }

    private fun tryLogin(username: String?, secret: String?) {
        Toast.makeText(this,"尝试自动登录，但还没做",Toast.LENGTH_SHORT).show()
    }

    fun initalize_Components() {
        ui_scroll_main = findViewById(R.id.ui_scroll_main)
        ui_include_viewpager_indicator = findViewById(R.id.ui_include_viewpager_indicator)
        ui_import_tv = ui_include_viewpager_indicator.findViewById(R.id.ui_import_tv)
        ui_export_tv = ui_include_viewpager_indicator.findViewById(R.id.ui_export_tv)
        ui_indicator_tv = ui_include_viewpager_indicator.findViewById(R.id.ui_indicator_tv)
        var ui_search_bar : View = findViewById(R.id.ui_include_search_bar)
        ui_search_bar.setOnClickListener {
            val intent = Intent()
            intent.setClass(this@MainActivity, SearchActivity::class.java)
            startActivity(intent)
        }
        var ui_profile_button: ImageView = findViewById(R.id.ui_search_user)
        ui_profile_button.setOnClickListener{
            val intent = Intent()
            intent.setClass(this@MainActivity, MenuActivity::class.java)
            startActivity(intent)
        }
    }

    fun set_viewpager_indicator(target: Float) {
        //
        val currentX: Float = ui_indicator_tv.translationX
        val animation = ObjectAnimator.ofFloat(ui_indicator_tv, "translationX", currentX, target)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        this.setStatusBarColor()
        super.onConfigurationChanged(newConfig)
    }

    private fun setStatusBarColor() {
        HiroUtils.setStatusBarColor(window, resources)
    }

}