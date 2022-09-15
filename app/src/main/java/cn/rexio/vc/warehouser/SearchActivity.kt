package cn.rexio.vc.warehouser

import HiroUtils.Factory.setStatusBarColor
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

class SearchActivity : Activity() {
    private lateinit var ui_search_bar : EditText
    private var searchMethod = 0
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
                    ui_search_bar.setText("")
                searchMethod = which
                ui_search_bar.hint = when (which) {
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
            val dialog = builder.create()
            dialog.show()
        }
        findViewById<ImageView>(R.id.ui_search_back).setOnClickListener {
            finish()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        setStatusBarColor(window,resources)
        super.onConfigurationChanged(newConfig)
    }
}