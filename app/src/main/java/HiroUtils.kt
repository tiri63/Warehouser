import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.view.Window
import androidx.core.view.WindowCompat


class HiroUtils{
    companion object Factory {
        fun setStatusBarColor(window : Window, resources : Resources)
        {
            val isLight = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO
            window.statusBarColor = if (isLight) Color.WHITE else Color.BLACK
            WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = isLight
        }
    }
}