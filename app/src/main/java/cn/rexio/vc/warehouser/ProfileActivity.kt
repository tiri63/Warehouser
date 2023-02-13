package cn.rexio.vc.warehouser

import HiroUtils
import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import cn.rexio.vc.warehouser.databinding.ActivityProfileBinding
import org.json.JSONArray
import org.json.JSONObject

class ProfileActivity : Activity() {
    private lateinit var bi: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar.let { it?.hide() }
        super.onCreate(savedInstanceState)
        bi = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(bi.root)
        HiroUtils.setPopWinStatusBarColor(window, resources)
        HiroUtils.animateView(bi.ui3ProfileTitle, 300, arrayOf(0f, 1f), arrayOf(200f, 0f, 0f, 0f), {}, {})
        HiroUtils.animateView(bi.ui3ProfileRoot, 300, arrayOf(0f, 1f), arrayOf(0f, 0f, 600f, 0f), {}, {})
        bi.ui3ProfileTitle.text = getString(R.string.txt_profile_hi, HiroUtils.userNickName)
        bi.ui3ProfileBack.setOnClickListener {
            this.finish()
        }
        bi.ui3ProfileUpdateBtn.setOnClickListener {
            if (bi.ui3ProfileOldText.text.toString() == "" || bi.ui3ProfileNewText.text.toString() == "")
                return@setOnClickListener
            if (bi.ui3ProfileNewText.text.toString() != bi.ui3ProfileConfirmText.text.toString()) {
                HiroUtils.logSnackBar(bi.root, getString(R.string.txt_profile_pwd_nomatch))
                return@setOnClickListener
            }
            HiroUtils.sendRequest("/user", arrayListOf("username", "token", "device", "action", "newPwd"),
                arrayListOf(
                    HiroUtils.userName ?: "null", HiroUtils.userToken ?: "null",
                    "mobile", "3", bi.ui3ProfileNewText.text.toString()
                ), {
                    try {
                        val json = JSONObject(it)
                        when(json["ret"])
                        {
                            "0" ->{
                                HiroUtils.logSnackBar(bi.root,getString(R.string.txt_successed))
                                this.finish()
                            }

                            else ->{
                                HiroUtils.parseJsonRet(bi.root, this, it, json["msg"] as String?)
                            }
                        }

                    } catch (ex: Exception) {
                        HiroUtils.logError(this, ex)
                    }
                }, {
                    HiroUtils.logSnackBar(bi.root, getString(R.string.txt_unable_to_connect))
                },
                this
            )
        }
    }

    override fun finish() {
        HiroUtils.animateView(bi.ui3ProfileRoot, 150, arrayOf(1f, 0f), arrayOf(0f, 0f, 0f, 600f), {}, {})
        HiroUtils.animateView(
            bi.ui3ProfileTitle,
            150,
            arrayOf(1f, 0f),
            arrayOf(0f, -200f, 0f, 0f),
            {},
            { super.finish() })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        HiroUtils.setPopWinStatusBarColor(window, resources)
        super.onConfigurationChanged(newConfig)
    }
}