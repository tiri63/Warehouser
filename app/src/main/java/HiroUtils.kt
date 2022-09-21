import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.WindowCompat
import okhttp3.*
import java.io.IOException


class HiroUtils {
    companion object Factory {
        var userName : String? = null
        var userNickName : String? = null
        fun setStatusBarColor(window: Window, resources: Resources) {
            val isLight =
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO
            window.statusBarColor = if (isLight) Color.WHITE else Color.BLACK
            WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
                isLight
        }

        fun sendRequest(
            url: String,
            paraName: List<String>,
            paraValue: List<String>,
            onResponse: (ret: String) -> Unit,
            onFailure: () -> Unit,
            success : String
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