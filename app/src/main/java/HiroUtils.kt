import android.animation.Animator
import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.view.View
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.WindowCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import okhttp3.*
import java.io.IOException


class HiroUtils {
    class Shelf(var main: String, var sub: String, var alias: String, var info: String) {}
    class Usage(var code: Int, var alias: String, var info: String) {}
    companion object Factory {
        var userName: String? = null
        var userNickName: String? = null
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

        fun viewAnimation(
            view: View,
            techniques: Techniques,
            duration: Long,
            beforeAnimationStart: (view: View) -> Unit,
            onAnimationEnd: (view: View) -> Unit
        ) {
            beforeAnimationStart(view)
            YoYo.with(techniques).repeatMode(0).withListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationEnd.invoke(view)
                    animation.removeAllListeners()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).duration(duration).interpolate(DecelerateInterpolator())
                .playOn(view).run {}
        }

        fun sendRequest(
            url: String,
            paraName: List<String>,
            paraValue: List<String>,
            onResponse: (ret: String) -> Unit,
            onFailure: () -> Unit,
            success: String
        ) {

            onResponse.invoke(success)

            /*val num = paraName.count().coerceAtMost(paraValue.count())
            val requestBodyBuilder = FormBody.Builder()
            for (lo in 0..num) {
                requestBodyBuilder.add(paraName[lo], paraValue[lo])
            }
            val requestBody = requestBodyBuilder.build()
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
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
    }
}