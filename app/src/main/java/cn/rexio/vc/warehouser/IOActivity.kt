package cn.rexio.vc.warehouser

import HiroUtils
import SpinnerAdapter
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import cn.rexio.vc.warehouser.databinding.ActivityIoBinding
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class IOActivity : Activity() {
    private lateinit var bi: ActivityIoBinding
    private var maxNum: Int = 0
    private var name: String = ""
    private var model: String = "x2"
    private var uid: String = "100000001"
    private var shelf: HiroUtils.Shelf = HiroUtils.Shelf("", "", "", "")
    private var usage: HiroUtils.Usage = HiroUtils.Usage(0, "", "")
    private var mode: Int = 0
    lateinit var usageAdapter: SpinnerAdapter<HiroUtils.Usage>
    lateinit var usagesubAdapter: SpinnerAdapter<HiroUtils.Usage>
    val usageList = arrayListOf<HiroUtils.Usage>()
    val usagesubList = arrayListOf<HiroUtils.Usage>()
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        bi = ActivityIoBinding.inflate(layoutInflater)
        setContentView(bi.root)
        HiroUtils.setPopWinStatusBarColor(window, resources)
        getExtraParameters()
        setExtraParameters()
        setSpinner()
        HiroUtils.animateView(bi.ui3IoRoot, 300, arrayOf(0f, 1f), arrayOf(0f, 0f, 600f, 0f), {}, {})
        initializeListeners()
    }

    override fun finish() {
        HiroUtils.animateView(bi.ui3IoRoot, 300, arrayOf(1f, 0f), arrayOf(0f, 0f, 0f, 600f), {}, { super.finish() })
    }

    private fun initializeListeners() {
        bi.ui3IoBack.setOnClickListener {
            this.finish()
        }
        bi.ui3IoReverse.setOnClickListener {
            changeMode()
        }
        bi.ui3IoLog.setOnClickListener {
            bi.ui3IoLog.isEnabled = false
            HiroUtils.sendRequest(
                "/log",
                arrayListOf("username", "token", "device", "action", "mshelf", "sshelf", "uid"),
                arrayListOf(
                    HiroUtils.userName ?: "null", HiroUtils.userToken ?: "null",
                    "mobile", "2", shelf.main, shelf.sub, uid
                ),
                {
                    try {
                        val json = JSONObject(it)
                        when (json["ret"]) {
                            "0" -> {
                                val ja = JSONArray(json["msg"].toString())
                                var txt = ""
                                for (i in 0 until ja.length()) {
                                    val jai = ja[i] as JSONObject
                                    txt =
                                        txt + "${timeStamp2Date(jai["time"].toString())} ${jai["nickname"]} " +
                                                "${
                                                    if (jai["action"] == "0") getString(R.string.txt_import) else getString(
                                                        R.string.txt_export
                                                    )
                                                } " +
                                                " ${jai["name"]} ${jai["count"]}${jai["unit"]}"
                                }
                                val intent = Intent(this@IOActivity, LogActivity::class.java)
                                intent.putExtra("log", txt)
                                startActivity(intent)
                            }

                            else -> {
                                HiroUtils.parseJsonRet(bi.root, this, it, json["msg"] as String?)
                            }
                        }
                    } catch (ex: Exception) {
                        HiroUtils.logError(this, ex)
                    }
                    bi.ui3IoLog.isEnabled = true
                },
                {
                    HiroUtils.logSnackBar(bi.root, getString(R.string.txt_unable_to_connect))
                    bi.ui3IoLog.isEnabled = true
                },
                "{\"ret\":\"0\",\"msg\":[{\"uid\":\"test01\",\"unit\":\"s\",\"functions\":\"1\",\"sshelf\":\"1\",\"mshelf\":\"1\",\"count\":\"20\",\"nickname\":\"Hiro\",\"name\":\"For Test Use Only\",\"action\":\"0\",\"time\":\"1666139123\",\"user\":\"hiro\"}]}"
            )
        }
        bi.ui3IoBtn.setOnClickListener {
            try {
                var current = bi.ui3IoCount.text.toString().toInt()
                if (current > maxNum) {
                    HiroUtils.logSnackBar(bi.root,getString(R.string.txt_io_format_error))
                    return@setOnClickListener
                }
                if (current < 0) {
                    HiroUtils.logSnackBar(bi.root,getString(R.string.txt_io_format_error))
                    return@setOnClickListener
                }
                bi.ui3IoCount.setText(current.toString())
            } catch (_: Exception) {
                HiroUtils.logSnackBar(bi.root,getString(R.string.txt_io_format_error))
                return@setOnClickListener
            }
            bi.ui3IoLoading.visibility = View.VISIBLE
            bi.ui3IoRoot.isEnabled = false
            val para = arrayListOf("username", "token", "device", "action", "shelf", "uid", "count", "usage")
            //0 for import and 1 for export, shelf is json
            val value =
                arrayListOf(
                    HiroUtils.userName ?: "null", HiroUtils.userToken ?: "null", "mobile", mode.toString(),
                    object : JSONObject() {
                        init {
                            put("main", shelf.main)
                            put("sub", shelf.sub)
                        }
                    }.toString(),
                    uid, bi.ui3IoCount.text.toString(), usage.code.toString()
                )
            HiroUtils.sendRequest(
                "/io", para, value,
                {
                    bi.ui3IoLoading.visibility = View.INVISIBLE
                    try {
                        val json = JSONObject(it)
                        when (json["ret"]) {
                            "0" -> {
                                HiroUtils.logSnackBar(bi.root, getString(R.string.txt_successed))
                            }

                            else -> {
                                HiroUtils.parseJsonRet(bi.root, this, it, json["msg"] as String?)
                            }
                        }
                    } catch (ex: Exception) {
                        HiroUtils.logError(this, ex)
                    }
                },
                {
                    bi.ui3IoLoading.visibility = View.INVISIBLE
                    HiroUtils.logSnackBar(bi.root, getString(R.string.txt_unable_to_connect))
                },
                "{\"ret\":\"0\"}"
            )
        }
        bi.ui3IoMin.setOnClickListener {
            bi.ui3IoCount.setText("0")
        }
        bi.ui3IoMax.setOnClickListener {
            bi.ui3IoCount.setText(maxNum.toString())
        }
        bi.ui3IoMinus.setOnClickListener {
            try {
                var current = bi.ui3IoCount.text.toString().toInt() - 1
                if (current < 0)
                    current = 0
                bi.ui3IoCount.setText(current.toString())
            } catch (_: Exception) {
                bi.ui3IoCount.setText("0")
            }
        }
        bi.ui3IoPlus.setOnClickListener {
            try {
                var current = bi.ui3IoCount.text.toString().toInt() + 1
                if (current > maxNum)
                    current = maxNum
                bi.ui3IoCount.setText(current.toString())
            } catch (_: Exception) {
                bi.ui3IoCount.setText(maxNum.toString())
            }
        }
    }

    fun timeStamp2Date(time: String): String {
        val timeLong = time.toLong() * 1000
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA) //要转换的时间格式
        val date: Date
        return try {
            date = sdf.parse(sdf.format(timeLong)) as Date
            sdf.format(date)
        } catch (_: Exception) {
            "{???}"
        }
    }

    override fun onBackPressed() {
        if (bi.root.isEnabled)
            super.onBackPressed()
    }

    private fun changeMode() {
        if (mode == 0) {
            mode = 1
            bi.ui3IoReverse.text = getText(R.string.txt_cast_to_import)
            bi.ui3IoBtn.text = getText(R.string.txt_export)
            HiroUtils.animateView(
                bi.ui3IoFor,
                200,
                arrayOf(1f, 0f),
                arrayOf(0f, -100f, 0f, 0f),
                { it.visibility = View.VISIBLE },
                { it.visibility = View.INVISIBLE })
            HiroUtils.animateView(
                bi.ui3IoForSpinner,
                200,
                arrayOf(0f, 1f),
                arrayOf(100f, 0f, 0f, 0f),
                { it.visibility = View.VISIBLE },
                {
                    it.visibility = View.VISIBLE
                })
            HiroUtils.animateView(
                bi.ui3IoForSpinnerSub,
                200,
                arrayOf(0f, 1f),
                arrayOf(100f, 0f, 0f, 0f),
                { it.visibility = View.VISIBLE },
                {
                    it.visibility = View.VISIBLE
                })
        } else {
            mode = 0
            bi.ui3IoReverse.text = getText(R.string.txt_cast_to_export)
            bi.ui3IoBtn.text = getText(R.string.txt_import)
            HiroUtils.animateView(
                bi.ui3IoFor,
                200,
                arrayOf(0f, 1f),
                arrayOf(-100f, 0f, 0f, 0f),
                { it.visibility = View.VISIBLE },
                { it.visibility = View.VISIBLE })
            HiroUtils.animateView(
                bi.ui3IoForSpinner,
                200,
                arrayOf(1f, 0f),
                arrayOf(0f, 100f, 0f, 0f),
                { it.visibility = View.VISIBLE },
                {
                    it.visibility = View.INVISIBLE
                })
            HiroUtils.animateView(
                bi.ui3IoForSpinnerSub,
                200,
                arrayOf(1f, 0f),
                arrayOf(0f, 100f, 0f, 0f),
                { it.visibility = View.VISIBLE },
                {
                    it.visibility = View.INVISIBLE
                })
        }
    }

    private fun setSpinner() {
        usageAdapter = SpinnerAdapter(this, usageList, "alias", HiroUtils.Usage::class.java)
        usagesubAdapter = SpinnerAdapter(this, usagesubList, "alias", HiroUtils.Usage::class.java)
        bi.ui3IoForSpinner.adapter = usageAdapter
        bi.ui3IoForSpinnerSub.adapter = usagesubAdapter
        usageList.add(HiroUtils.Usage(0, "维修", "维修"))
        usageList.add(HiroUtils.Usage(1, "电机", "电机"))
        usageList.add(HiroUtils.Usage(2, "器件", "器件"))
        usageList.add(HiroUtils.Usage(3, "冲天炉", "冲天炉"))
        usageList.add(HiroUtils.Usage(4, "办公室", "办公室"))
        usageAdapter.notifyDataSetChanged()
        usagesubList.add(HiroUtils.Usage(0, "借出", "借出"))
        usagesubList.add(HiroUtils.Usage(1, "维护", "维护"))
        usagesubList.add(HiroUtils.Usage(2, "废弃", "废弃"))
        usagesubAdapter.notifyDataSetChanged()
    }

    private fun getExtraParameters() {
        maxNum = intent.getIntExtra("maxNum", 0)
        usage.code = intent.getIntExtra("usage.code", 0)
        intent.getStringExtra("name")?.let { name = it }
        intent.getStringExtra("model")?.let { model = it }
        intent.getStringExtra("uid")?.let { uid = it }
        intent.getStringExtra("shelf.main")?.let { shelf.main = it }
        intent.getStringExtra("shelf.sub")?.let { shelf.sub = it }
        intent.getStringExtra("shelf.alias")?.let { shelf.alias = it }
        intent.getStringExtra("shelf.info")?.let { shelf.info = it }
        intent.getStringExtra("usage.alias")?.let { usage.alias = it }
        intent.getStringExtra("usage.info")?.let { usage.info = it }
    }

    private fun setExtraParameters() {
        bi.ui3IoTitle.text = name
        bi.ui3IoModel.text = model
        bi.ui3IoUid.text = uid
        bi.ui3IoShelf.text = "${shelf.main} - ${shelf.sub} (${shelf.alias})"
        bi.ui3IoFor.text = usage.alias
        bi.ui3IoLeft.text = String.format(getText(R.string.txt_dialog_item_count_left).toString(), maxNum)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        HiroUtils.setPopWinStatusBarColor(window, resources)
        super.onConfigurationChanged(newConfig)
    }
}