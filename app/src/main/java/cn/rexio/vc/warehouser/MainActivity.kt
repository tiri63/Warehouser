package cn.rexio.vc.warehouser

import HiroUtils
import SharedPref
import android.app.Activity
import android.app.AlertDialog;
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur


class MainActivity : Activity() {
    private var username: String? = null
    private var secret: String? = null
    private lateinit var ui_scroll_main: ConstraintLayout
    private var ui_current = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar.let { it?.hide() }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        initalize_Components()
        setStatusBarColor()
        loadSettings()
        fakeData()
    }

    fun loadSettings() {
        username = SharedPref(this, "username", "null").getValue(this, this::username)
        secret = SharedPref(this, "secret", "null").getValue(this, this::secret)
        if (username.equals("null") || secret.equals("null"))
        {
            //Blurry.with(this).capture(findViewById(R.id.ui_main_layout)).into(findViewById(R.id.ui_login_background))
            login()
        }
        else
            tryLogin(username, secret)
        // from View
        findViewById<BlurView>(R.id.ui_search_back_blur).setupWith(findViewById<ViewGroup>(R.id.ui_search_back_constraint), RenderScriptBlur(this)) // or RenderEffectBlur
            .setBlurRadius(20f)
        //Blurry.with(this).radius(10).sampling(8).color(Color.WHITE).async().onto(findViewById(R.id.ui_login_frame))
    }

    fun login() {
        var rootView = findViewById<ViewGroup>(R.id.ui_main_layout)
        var blurView = findViewById<BlurView>(R.id.ui_login_background)
        rootView.visibility = View.VISIBLE
        var radius = 20f


        // Optional:
        // Set drawable to draw in the beginning of each blurred frame.
        // Can be used in case your layout has a lot of transparent space and your content
        // gets a too low alpha value after blur is applied.
        var windowBackground : Drawable = window.decorView.background

        blurView.setupWith(rootView, RenderScriptBlur(this)) // or RenderEffectBlur
            .setFrameClearDrawable(windowBackground) // Optional
            .setBlurRadius(radius)
        blurView.findViewById<TextView>(R.id.dialog_login).setOnClickListener {
            Toast.makeText(
                this,
                blurView.findViewById<EditText>(R.id.login_name).text.toString() + " :: " +
                        blurView.findViewById<EditText>(R.id.login_password).text.toString(),
                Toast.LENGTH_SHORT
            ).show()
            SharedPref(this, "username", "null").setValue(
                this,
                this::username,
                blurView.findViewById<EditText>(R.id.login_name).text.toString()
            )
            SharedPref(this, "secret", "null").setValue(
                this,
                this::secret,
                blurView.findViewById<EditText>(R.id.login_password).text.toString()
            )
            blurView.visibility = View.INVISIBLE
        }
        /*val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        val view = layoutInflater.inflate(R.layout.dialog_login, null, false)
        builder.setView(view)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        }*/
    }

    private fun tryLogin(username: String?, secret: String?) {
        Toast.makeText(this,"尝试自动登录，但还没做",Toast.LENGTH_SHORT).show()
    }

    fun initalize_Components() {
        ui_scroll_main = findViewById(R.id.ui_scroll_main)
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
        findViewById<FloatingActionButton>(R.id.ui_import_fab).setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setCancelable(true)
            val view = window.layoutInflater.inflate(R.layout.dialog_import_new_item, null, false)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        this.setStatusBarColor()
        super.onConfigurationChanged(newConfig)
    }

    private fun setStatusBarColor() {
        HiroUtils.setStatusBarColor(window, resources)
    }

    private fun fakeData()  {
        val mRecyclerView = findViewById<View>(R.id.ui_include_main_function).findViewById<RecyclerView>(R.id.ui_shelf_item_list)
        var mRecyclerAdapter = ShelfAdapter(
            arrayListOf("螺丝","螺帽","螺母","扳手","123","321","1234","4321","28","sdj","dff","ioa","fi","129","do","0fj"),
            arrayListOf(10,11,12,13,12,1,3,5,8,5,3,89,0,3,2,6),
            arrayListOf("150mm-X-1","METAL-X-2","STEEL-X",null,"s-1","u-9","o-0","p-1","i-1","p-9","m-0","h-a","p-1","???","{0-x}","[a]"),
            arrayListOf("10000001","10000002","10000003","114514",null,"2135","6643","46516","463642","135","1364","1354","3146","1346","136","13477"),
            arrayListOf("仓库2-货架1-第1层","仓库3-货架1-第1层","仓库2-货架9-第1层","仓库2-货架1-第6层","仓库2-货架1-第6层","仓库2-货架1-第6层","仓库2-货架1-第6层","仓库2-货架1-第6层","仓库2-货架1-第6层","仓库2-货架1-第6层","仓库2-货架1-第6层","仓库2-货架1-第6层","仓库2-货架1-第6层","仓库2-货架1-第6层","仓库2-货架1-第6层","仓库2-货架1-第6层"),
            arrayListOf("冲天炉","维修","造型","熔炼","维修","造型","熔炼","维修","造型",null,"维修","造型","熔炼","维修","造型","熔炼"),
            this, window
        )
        mRecyclerView.adapter = mRecyclerAdapter
    }


}