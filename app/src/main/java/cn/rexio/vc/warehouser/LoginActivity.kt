package cn.rexio.vc.warehouser

import SharedPref
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.WindowCompat
import cn.rexio.vc.warehouser.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import kotlin.system.exitProcess

class LoginActivity : Activity() {

    private lateinit var bi: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        bi = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bi.root)

        actionBar.let { it?.hide() }


        bi.ui3LoginBtn.setOnClickListener {
            bi.uiLoginLogging.visibility = View.VISIBLE
            bi.ui3LoginBtn.isEnabled = false
            bi.ui3LoginForgetpwd.isEnabled = false
            HiroUtils.sendRequest("baseURL/login", arrayListOf("username", "password"),
                arrayListOf(
                    bi.ui3LoginUsernameText.text.toString(),
                    bi.ui3LoginPwdText.text.toString()
                ),
                {
                    if (it == "success") {
                        bi.uiLoginLogging.visibility = View.GONE
                        SharedPref(this, "username", "null").setValue(
                            this,
                            MainActivity::username,
                            bi.ui3LoginUsernameText.text.toString()
                        )
                        SharedPref(this, "secret", "null").setValue(
                            this,
                            MainActivity::username,
                            bi.ui3LoginPwdText.text.toString()
                        )
                        this.finish()
                    } else {
                        Snackbar.make(bi.root,R.string.txt_login_failed,Snackbar.LENGTH_SHORT).show()
                    }
                    bi.ui3LoginBtn.isEnabled = true
                    bi.ui3LoginForgetpwd.isEnabled = true
                }, {
                    Snackbar.make(bi.root,R.string.txt_unable_to_connect,Snackbar.LENGTH_SHORT).show()
                    bi.ui3LoginBtn.isEnabled = true
                    bi.ui3LoginForgetpwd.isEnabled = true
                },
                "success"
            )
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}