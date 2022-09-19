package cn.rexio.vc.warehouser

import HiroUtils
import SharedPref
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur


class MainActivity : Activity() {
    private var username: String? = null
    private var secret: String? = null
    private lateinit var ui_scroll_main: ConstraintLayout
    private var searchMethod = 0
    private var searchMethodD = 0

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
            login()
        }
        else
            tryLogin(username, secret)
    }

    fun login() {
        var rootView = findViewById<ViewGroup>(R.id.ui_main_layout)
        var blurView = findViewById<BlurView>(R.id.ui_login_background)
        rootView.visibility = View.VISIBLE
        var radius = 20f


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
            blurView.visibility = View.GONE
        }
    }

    private fun tryLogin(username: String?, secret: String?) {
        Toast.makeText(this,"尝试自动登录，但还没做",Toast.LENGTH_SHORT).show()
    }

    fun initalize_Components() {
        ui_scroll_main = findViewById(R.id.ui_scroll_main)
        var ui_search_bar : View = findViewById(R.id.ui_include_search_bar)
        var ui_search = findViewById<View>(R.id.ui_include_search_function)
        var ui_search_txt = ui_search.findViewById<EditText>(R.id.ui_search_bar)
        ui_search_bar.setOnClickListener {
            ui_search_bar.visibility = View.GONE
            ui_search.visibility = View.VISIBLE
            ui_search.findViewById<ImageView>(R.id.ui_search_scan).visibility = if(searchMethod == 0) View.VISIBLE else View.GONE
            ui_search.findViewById<ImageView>(R.id.ui_search_scan).setOnClickListener {
                startCapture(0x02)
            }
            ui_search_txt.requestFocus()
            showInputMethod(this@MainActivity,ui_search_txt)
        }
        var ui_profile_button: ImageView = findViewById(R.id.ui_search_user)
        ui_profile_button.setOnClickListener{
            val intent = Intent()
            intent.setClass(this@MainActivity, MenuActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.ui_import_btn).setOnClickListener {
            hideInputMethod(this@MainActivity)
            var ui_fake_dialog = findViewById<View>(R.id.ui_dialog_import_fake)
            ui_fake_dialog.visibility = View.VISIBLE
            var ui_dialog_include_main = ui_fake_dialog.findViewById<View>(R.id.ui_include_dialog_item)
            var ui_dialog_include_search = ui_fake_dialog.findViewById<View>(R.id.ui_include_dialog_search)
            ui_dialog_include_main.findViewById<ImageView>(R.id.ui_search_scan).setOnClickListener {
                startCapture()
            }
            ui_dialog_include_main.findViewById<TextView>(R.id.dialog_cancel).setOnClickListener {
                ui_fake_dialog.visibility = View.GONE
            }
            ui_dialog_include_main.findViewById<ImageView>(R.id.ui_search_img).setOnClickListener {
                ui_dialog_include_main.visibility = View.GONE
                ui_dialog_include_search.visibility = View.VISIBLE
            }

            ui_dialog_include_search.findViewById<ImageView>(R.id.ui_search_back).setOnClickListener {
                ui_dialog_include_main.visibility = View.VISIBLE
                ui_dialog_include_search.visibility = View.GONE

            }
            var ui_fake_dialog_btn = ui_fake_dialog.findViewById<TextView>(R.id.ui_search_method)
            var ui_fake_dialog_edit = ui_fake_dialog.findViewById<EditText>(R.id.ui_search_bar)
            ui_dialog_include_search.findViewById<ImageView>(R.id.ui_search_scan).visibility = View.GONE
            ui_fake_dialog_btn.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setCancelable(true)
                builder.setSingleChoiceItems(arrayOf(
                    getText(R.string.txt_search_method_name).toString(),
                    getText(R.string.txt_search_method_id).toString(),
                    getText(R.string.txt_search_method_model).toString()),
                    searchMethod
                ) { dialog, which ->
                    dialog.dismiss()
                    if(searchMethodD != which)
                        ui_fake_dialog_edit.setText("")
                    searchMethodD = which
                    ui_fake_dialog_edit.hint = when (which) {
                        0 -> getText(R.string.txt_search_via_name)
                        1 -> getText(R.string.txt_search_via_id)
                        else -> getText(R.string.txt_search_via_model)
                    }
                    ui_fake_dialog_btn.setText(when (which) {
                        0 -> getText(R.string.txt_search_method_name)
                        1 -> getText(R.string.txt_search_method_id)
                        else -> getText(R.string.txt_search_method_model)
                    })
                }
                hideInputMethod(this@MainActivity)
                val dialog = builder.create()
                dialog.setOnDismissListener {
                    ui_fake_dialog_edit.requestFocus()
                    showInputMethod(this@MainActivity,ui_fake_dialog_edit)
                }
                dialog.show()
            }

        }
        ui_search_txt.setOnClickListener{

        }
        var ui_search_method = ui_search.findViewById<TextView>(R.id.ui_search_method)
        ui_search_method.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setCancelable(true)
            builder.setSingleChoiceItems(arrayOf(getText(R.string.txt_search_method_shelf).toString(),
                getText(R.string.txt_search_method_name).toString(),
                getText(R.string.txt_search_method_id).toString(),
                getText(R.string.txt_search_method_model).toString()),
                searchMethod
            ) { dialog, which ->
                dialog.dismiss()
                if(searchMethod != which)
                    ui_search_txt.setText("")
                searchMethod = which
                ui_search.findViewById<ImageView>(R.id.ui_search_scan).visibility = if(searchMethod == 0) View.VISIBLE else View.GONE
                ui_search_txt.hint = when (which) {
                    0 -> getText(R.string.txt_search_via_shelf)
                    1 -> getText(R.string.txt_search_via_name)
                    2 -> getText(R.string.txt_search_via_id)
                    else -> getText(R.string.txt_search_via_model)
                }
                ui_search_method.text = when (which) {
                    0 -> getText(R.string.txt_search_method_shelf)
                    1 -> getText(R.string.txt_search_method_name)
                    2 -> getText(R.string.txt_search_method_id)
                    else -> getText(R.string.txt_search_method_model)
                }
            }
            hideInputMethod(this@MainActivity)
            val dialog = builder.create()
            dialog.setOnDismissListener {
                ui_search_txt.requestFocus()
                showInputMethod(this@MainActivity,ui_search_txt)
            }
            dialog.show()
        }
        findViewById<ImageView>(R.id.ui_search_back).setOnClickListener {
            hideInputMethod(this@MainActivity)
            ui_search_bar.visibility = View.VISIBLE
            ui_search.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        var ui_search = findViewById<View>(R.id.ui_include_search_function)
        var ui_search_bar : View = findViewById(R.id.ui_include_search_bar)
        var ui_fake_dialog = findViewById<View>(R.id.ui_dialog_import_fake)
        if(ui_search.visibility == View.VISIBLE) {
            hideInputMethod(this@MainActivity)
            ui_search_bar.visibility = View.VISIBLE
            ui_search.visibility = View.GONE
        }
        else if(ui_fake_dialog.visibility == View.VISIBLE)
        {
            hideInputMethod(this@MainActivity)
            ui_fake_dialog.visibility = View.GONE
        }
        else
            super.onBackPressed()
    }

    fun startCapture(retCode: Int = 0x01)
    {
        // 申请权限之后，调用DefaultView扫码界面。
        ScanUtil.startScan(this@MainActivity, retCode,  HmsScanAnalyzerOptions.Creator().setHmsScanTypes(
            HmsScan.ALL_SCAN_TYPE).create())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) {
            return
        }
        // 从onActivityResult返回data中，用ScanUtil.RESULT作为key值取到HmsScan返回值。
        else if (requestCode == 0x01) {
            when (val obj: Parcelable? = data.getParcelableExtra(ScanUtil.RESULT)) {
                is HmsScan -> {
                    if (!TextUtils.isEmpty(obj.getOriginalValue())) {
                        var ui_fake_dialog = findViewById<View>(R.id.ui_dialog_import_fake)
                        Toast.makeText(this, obj.getOriginalValue(), Toast.LENGTH_SHORT).show()
                        ui_fake_dialog.findViewById<View>(R.id.ui_include_dialog_item).findViewById<TextView>(R.id.dialog_shelf)
                            .text = obj.getOriginalValue()
                        ui_fake_dialog.visibility = View.GONE
                    }
                    return
                }
            }
        }
        else if(requestCode == 0x02)
        {
            when (val obj: Parcelable? = data.getParcelableExtra(ScanUtil.RESULT)) {
                is HmsScan -> {
                    if (!TextUtils.isEmpty(obj.getOriginalValue())) {
                        var ui_search_txt = findViewById<View>(R.id.ui_include_search_function).findViewById<EditText>(R.id.ui_search_bar)
                        Toast.makeText(this, obj.getOriginalValue(), Toast.LENGTH_SHORT).show()
                        ui_search_txt.setText(obj.getOriginalValue())
                    }
                    return
                }
            }
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

    fun showInputMethod(activity: Activity, editText: EditText?) {
        val inputMethodManager: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideInputMethod(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive && activity.currentFocus != null) {
            if (activity.currentFocus!!.windowToken != null) {
                inputMethodManager.hideSoftInputFromWindow(
                    activity.currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
    }

}