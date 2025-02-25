import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.*
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.WindowCompat
import cn.rexio.vc.warehouser.LoginActivity
import cn.rexio.vc.warehouser.MainActivity
import cn.rexio.vc.warehouser.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okio.IOException
import org.json.JSONObject


class HiroUtils {
    class Shelf(var depart: String, var main: String, var sub: String, var alias: String?, var info: String?) {}
    class Usage(var code: Int, var alias: String, var info: String, var hide: Boolean) {}
    companion object Factory {
        @SuppressLint("StaticFieldLeak")
        var mainWindowContext: Context? = null
        var userName: String? = null
        var userNickName: String? = null
        var userToken: String? = null

        var baseURL = "http://10.3.201.64/warehouser"
        //var baseURL = "http://101.34.8.69/warehouser"//hiro's server
        var usageArray = ArrayList<Usage>()
        var userDepart: String? = null
        fun setStatusBarColor(window: Window, resources: Resources) {
            val isLight =
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO
            window.statusBarColor = if (isLight) Color.WHITE else Color.BLACK
            WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
                isLight
        }

        fun setPopWinStatusBarColor(window: Window, resources: Resources) {
            val isLight =
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO
            window.statusBarColor = 33000000
            WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
                isLight

        }

        fun parseShelf(str: JSONObject): Shelf {
            return try {
                val a = if (str.has("alias")) str["alias"] as String? else null
                val d = if (str.has("desp")) str["desp"] as String? else null
                val shelf = str["fid"] as String
                val sarray = shelf.split("-")
                if (sarray.size >= 3) {
                    Shelf(sarray[0], sarray[1], sarray[2], a, d)
                } else {
                    Shelf("-1", "-1", "-1", a, d)
                }
            } catch (ex: Exception) {
                Shelf("-1", "-1", "-1", null, null)
            }
        }

        fun animateView(
            view: View,
            duration: Long,
            alpha: Array<Float>,
            translation: Array<Float>,
            beforeAnimationStart: (view: View) -> Unit,
            afterAnimation: (view: View) -> Unit
        ) {
            beforeAnimationStart(view)
            val pva = PropertyValuesHolder.ofFloat("alpha", alpha[0], alpha[1])
            val pvb = PropertyValuesHolder.ofFloat("translationX", translation[0], translation[1])
            val pvc = PropertyValuesHolder.ofFloat("translationY", translation[2], translation[3])
            val oa = ObjectAnimator.ofPropertyValuesHolder(view, pva, pvb, pvc)
            oa.duration = duration
            oa.interpolator = DecelerateInterpolator()
            oa.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    afterAnimation(view)
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }

            })
            oa.start()
        }

        fun sendRequest(
            url: String,
            paraName: List<String>,
            paraValue: List<String>,
            onResponse: (ret: String) -> Unit,
            onFailure: () -> Unit,
            context: Context? = null
        ) {
            var ld: AlertDialog? = null
            context?.let {
                ld = loadingDialog(context)
            }
            val x_url = baseURL + url
            //onResponse.invoke(success)

            val num = paraName.count().coerceAtMost(paraValue.count())
            val hBuilder = Headers.Builder()
            for (lo in 0 until num) {
                hBuilder.add(paraName[lo], paraValue[lo])
            }
            val request = Request.Builder()
                .url(x_url)
                .post(FormBody.Builder().build())
                .headers(hBuilder.build())
                .build()
            OkHttpClient().newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        GlobalScope.launch(Dispatchers.Main) {
                            ld?.dismiss()
                            onFailure.invoke()
                            e.localizedMessage?.let { Log.e("internet", it) }
                            Log.e("internet", e.stackTraceToString())
                            //mainWindowContext?.let { logError(it, e) }
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        GlobalScope.launch(Dispatchers.Main) {
                            ld?.dismiss()
                            response.body?.let {
                                onResponse.invoke(it.string())
                            }
                            //Toast.makeText(mainWindowContext,it.string(),Toast.LENGTH_SHORT).show()}

                            //mainWindowContext?.let { response.body?.let { it1 -> logWin(it,"ret", it1.string()) } }}
                        }
                    }
                })
        }

        fun logOut() {
            mainWindowContext?.let {
                SharedPref(it, "username", "null").setValue(
                    this,
                    MainActivity::username,
                    "null"
                )
                SharedPref(it, "secret", "null").setValue(
                    this,
                    MainActivity::username,
                    "null"
                )
                val intent = Intent().setClass(it, LoginActivity::class.java)
                it.startActivity(intent)
            }

        }

        fun logError(context: Context, ex: Exception) {
            logWin(
                context,
                context.getString(R.string.txt_exception),
                ex::class.java.toString() + "\r\n" + ex.localizedMessage
            )
            Log.e("error",ex.stackTraceToString())
        }

        fun logError(context: Context, ex: Exception, onBtnClicked: () -> Unit) {
            logWin(
                context,
                context.getString(R.string.txt_exception),
                ex::class.java.toString() + "\r\n" + ex.localizedMessage, onBtnClicked
            )
        }

        fun logWin(context: Context, title: String, content: String) {
            try {
                MaterialAlertDialogBuilder(context).setTitle(title)
                    .setMessage(content)
                    .setPositiveButton(R.string.txt_ok) { dialogInterface: DialogInterface, i: Int ->
                        dialogInterface.dismiss()
                    }.create().show()
            } catch (ex: Exception) {
                Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
            }

        }

        fun logWin(context: Context, title: String, content: String, OnBtnClicked: () -> Unit) {
            try {
                MaterialAlertDialogBuilder(context).setTitle(title)
                    .setMessage(content)
                    .setPositiveButton(R.string.txt_ok) { dialogInterface: DialogInterface, i: Int ->
                        OnBtnClicked()
                        dialogInterface.dismiss()
                    }.create().show()
            } catch (ex: Exception) {
                Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
                OnBtnClicked()
            }

        }

        fun logSnackBar(view: View, ex: Exception) {
            logSnackBar(view, ex.cause.toString() + "\r\n" + ex.localizedMessage)
        }

        fun logSnackBar(view: View, context: Context, stringId: Int) {
            logSnackBar(view, context.getString(stringId))
        }

        fun logSnackBar(view: View, cs: CharSequence) {
            logSnackBar(view, cs.toString())
        }

        fun logSnackBar(view: View, str: String) {
            Snackbar.make(view, str, Snackbar.LENGTH_SHORT).show()
        }

        fun showInputMethod(activity: Activity, editText: EditText?) {
            val inputMethodManager: InputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }

        fun hideInputMethod(activity: Activity) {
            val inputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.isActive && activity.currentFocus != null) {
                if (activity.currentFocus!!.windowToken != null) {
                    inputMethodManager.hideSoftInputFromWindow(
                        activity.currentFocus!!.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }
            }
        }

        fun parseJsonRet(view: View, context: Context, json: String?, msg: String?) {
            json?.let {
                val js = JSONObject(json)
                when (js["ret"]) {
                    "ec-003" -> {
                        logSnackBar(view, context.getString(R.string.txt_privilege_insufficient_title))
                    }

                    "ec-011" -> {
                        logSnackBar(view, context.getString(R.string.txt_token_expired_title))
                        logOut()
                    }

                    null -> {
                        logSnackBar(view, context.getString(R.string.txt_unable_to_connect))
                    }

                    else -> {
                        logWin(
                            context, msg ?: context.getString(R.string.txt_no_details),
                            json
                        )
                    }
                }
            }

        }

        fun loadingDialog(context: Context): AlertDialog {
            return AlertDialog.Builder(context).setView(R.layout.activity_loading).create()
        }
    }
}