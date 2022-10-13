package cn.rexio.vc.warehouser

import HiroUtils
import SpinnerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.view.WindowCompat
import cn.rexio.vc.warehouser.databinding.ActivityIoBinding

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
        //overridePendingTransition(R.anim.ani_slide_up, R.anim.ani_slide_down)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        bi = ActivityIoBinding.inflate(layoutInflater)
        setContentView(bi.root)
        HiroUtils.setPopWinStatusBarColor(window, resources)
        getExtraParameters()
        setExtraParameters()
        setSpinner()
        HiroUtils.animateView(bi.ui3IoRoot, 300, arrayOf(0f, 1f), arrayOf(0f, 0f, 600f, 0f), {}, {})
        bi.ui3IoBack.setOnClickListener {
            this.finish()
        }
        bi.ui3IoReverse.setOnClickListener {
            changeMode()
        }
        bi.ui3IoLog.setOnClickListener {
            val intent = Intent(this@IOActivity, LogActivity::class.java)
            intent.putExtra(
                "log",
                "2022-9-20 13:05:00 李四从仓库1-货架2-第4层 出库了 50个 螺丝（型号: A1, 唯一编码: 10000001），用于运输工作。\n" +
                        "2022-9-21 16:00:00 张三向仓库1-货架2-第4层 入库了 70个 螺丝（型号: X2, 唯一编码: 10000004），并标记为运输用\n" +
                        "2022-9-21 16:00:00 张三向仓库1-货架2-第4层 入库了 70个 螺丝（型号: X2, 唯一编码: 10000004），并标记为运输用\n" +
                        "2022-9-21 16:00:00 张三向仓库1-货架2-第4层 入库了 70个 螺丝（型号: X2, 唯一编码: 10000004），并标记为运输用\n" +
                        "2022-9-21 16:00:00 张三向仓库1-货架2-第4层 入库了 70个 螺丝（型号: X2, 唯一编码: 10000004），并标记为运输用\n" +
                        "2022-9-21 16:00:00 张三向仓库1-货架2-第4层 入库了 70个 螺丝（型号: X2, 唯一编码: 10000004），并标记为运输用"
            )
            startActivity(intent)
        }
    }

    override fun finish() {
        HiroUtils.animateView(bi.ui3IoRoot, 300, arrayOf(1f, 0f), arrayOf(0f, 0f, 0f, 600f), {}, { super.finish() })
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