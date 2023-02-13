package cn.rexio.vc.warehouser

import HiroUtils
import SharedPref
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.WindowCompat
import cn.rexio.vc.warehouser.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import kotlin.system.exitProcess

class LoginActivity : Activity() {

    private lateinit var bi: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        bi = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bi.root)

        actionBar.let { it?.hide() }
        HiroUtils.setStatusBarColor(window, resources)
        bi.ui3LoginForgetpwd.setOnClickListener {
            HiroUtils.logSnackBar(bi.root,getString(R.string.txt_forget_pwd_tip))
        }
        bi.ui3LoginBtn.setOnClickListener {
            bi.uiLoginLogging.visibility = View.VISIBLE
            bi.ui3LoginBtn.isEnabled = false
            bi.ui3LoginForgetpwd.isEnabled = false
            HiroUtils.sendRequest("/user", arrayListOf("action", "username", "password", "device"),
                arrayListOf(
                    "1",
                    bi.ui3LoginUsernameText.text.toString(),
                    bi.ui3LoginPwdText.text.toString(),
                    "mobile"
                ),
                {
                    try {
                        val json = JSONObject(it)
                        when (json["ret"]) {
                            "0" -> {
                                bi.uiLoginLogging.visibility = View.GONE
                                SharedPref(this, "username", "null").setValue(
                                    this,
                                    MainActivity::username,
                                    bi.ui3LoginUsernameText.text.toString()
                                )
                                HiroUtils.userName = bi.ui3LoginUsernameText.text.toString()
                                SharedPref(this, "secret", "null").setValue(
                                    this,
                                    MainActivity::username,
                                    json["token"].toString()
                                )
                                HiroUtils.userToken = json["token"].toString()
                                SharedPref(this, "nickname", "null").setValue(
                                    this,
                                    MainActivity::username,
                                    json["nickname"].toString()
                                )
                                HiroUtils.userNickName = json["nickname"].toString()
                                SharedPref(this, "depart", "null").setValue(
                                    this,
                                    MainActivity::username,
                                    json["depart"].toString()
                                )
                                HiroUtils.userDepart = json["depart"].toString()
                                this.finish()
                            }

                            else -> {
                                HiroUtils.logSnackBar(bi.root, getString(R.string.txt_login_failed))
                            }
                        }
                    } catch (ex: Exception) {
                        HiroUtils.logError(this, ex)
                    }
                    bi.ui3LoginBtn.isEnabled = true
                    bi.ui3LoginForgetpwd.isEnabled = true
                }, {
                    HiroUtils.logSnackBar(bi.root, getString(R.string.txt_unable_to_connect))
                    bi.ui3LoginBtn.isEnabled = true
                    bi.ui3LoginForgetpwd.isEnabled = true
                },
                null
            )
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        HiroUtils.setStatusBarColor(window, resources)
        super.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}