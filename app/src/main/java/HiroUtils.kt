import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.view.View
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.WindowCompat
import cn.rexio.vc.warehouser.LoginActivity
import cn.rexio.vc.warehouser.MainActivity
import cn.rexio.vc.warehouser.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import okhttp3.*
import org.json.JSONObject


class HiroUtils {
    class Shelf(var main: String, var sub: String, var alias: String?, var info: String?) {}
    class Usage(var code: Int, var alias: String, var info: String) {}
    companion object Factory {
        @SuppressLint("StaticFieldLeak")
        var mainWindowContext: Context? = null
        var userName: String? = null
        var userNickName: String? = null
        var userToken: String? = null
        var baseURL = "http://10.3.201.64/warehouser"
        var usageArray = ArrayList<Usage>()
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
            try {
                val m = str["mshelf"] as String
                val s = str["sshelf"] as String
                val a = if (str.has("alias")) str["alias"] as String? else null
                val d = if (str.has("desp"))  str["desp"] as String? else null
                return Shelf(m, s, a, d)
            } catch (ex: Exception) {
                return Shelf("-1", "-1", null, null)
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
            success: String
        ) {
            val x_url = baseURL + url
            onResponse.invoke(success)

            /*val num = paraName.count().coerceAtMost(paraValue.count())
            val hBuilder = Headers.Builder()
            for (lo in 0..num) {
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
                        onFailure.invoke()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        onResponse.invoke(response.message)
                    }
                })*/
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
        }

        fun logWin(context: Context, title: String, content: String) {
            MaterialAlertDialogBuilder(context).setTitle(title)
                .setMessage(content).setPositiveButton(R.string.txt_ok) { dialogInterface: DialogInterface, i: Int ->
                    mainWindowContext?.let {
                        val mc = it.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        mc.setPrimaryClip(ClipData.newPlainText("error", content))
                    }

                    dialogInterface.dismiss()
                }.create().show()
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
            when (json) {
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
                        context, json,
                        msg ?: context.getString(R.string.txt_no_details)
                    )
                }
            }
        }
    }
}