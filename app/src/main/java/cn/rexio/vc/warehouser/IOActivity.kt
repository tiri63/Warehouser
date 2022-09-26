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
import com.daimajia.androidanimations.library.Techniques

class IOActivity : Activity() {
    private lateinit var bi: ActivityIoBinding
    private var maxNum: Int = 0
    private var name: String = ""
    private var model: String = "x2"
    private var uid: String = "100000001"
    private var shelf: HiroUtils.Shelf = HiroUtils.Shelf("","","","")
    private var usage: HiroUtils.Usage = HiroUtils.Usage(0,"","")
    private var mode : Int = 0
    lateinit var usageAdapter:SpinnerAdapter<HiroUtils.Usage>
    lateinit var usagesubAdapter:SpinnerAdapter<HiroUtils.Usage>
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
        bi.ui3IoBack.setOnClickListener {
            this.finish()
        }
        bi.ui3IoReverse.setOnClickListener {
            changeMode()
        }
        bi.ui3IoLog.setOnClickListener {
            val intent = Intent(this@IOActivity,LogActivity::class.java)
            intent.putExtra("log","2022-9-20 13:05:00 李四从仓库1-货架2-第4层 出库了 50个 螺丝（型号: A1, 唯一编码: 10000001），用于运输工作。\r\n2022-9-21 16:00:00 张三向仓库1-货架2-第4层 入库了 70个 螺丝（型号: X2, 唯一编码: 10000004），并标记为运输用")
            startActivity(intent)
        }
    }

    private fun changeMode()
    {
        if(mode == 0)
        {
            mode = 1
            bi.ui3IoReverse.text = getText(R.string.txt_cast_to_import)
            bi.ui3IoBtn.text = getText(R.string.txt_export)
            HiroUtils.viewAnimation(bi.ui3IoFor,Techniques.FadeOutLeft,200,{
                it.visibility = View.VISIBLE
            },{
                it.visibility = View.INVISIBLE
            })
            HiroUtils.viewAnimation(bi.ui3IoForSpinner,Techniques.FadeInRight,200,{
                it.visibility = View.VISIBLE
            },{
                it.visibility = View.VISIBLE
            })
            HiroUtils.viewAnimation(bi.ui3IoForSpinnerSub,Techniques.FadeInRight,200,{
                it.visibility = View.VISIBLE
            },{
                it.visibility = View.VISIBLE
            })
        }
        else
        {
            mode = 0
            bi.ui3IoReverse.text = getText(R.string.txt_cast_to_export)
            bi.ui3IoBtn.text = getText(R.string.txt_import)
            HiroUtils.viewAnimation(bi.ui3IoFor,Techniques.FadeInLeft,200,{
                it.visibility = View.VISIBLE
            },{
                it.visibility = View.VISIBLE
            })
            HiroUtils.viewAnimation(bi.ui3IoForSpinner,Techniques.FadeOutRight,200,{
                it.visibility = View.VISIBLE
            },{
                it.visibility = View.INVISIBLE
            })
            HiroUtils.viewAnimation(bi.ui3IoForSpinnerSub,Techniques.FadeOutRight,200,{
                it.visibility = View.VISIBLE
            },{
                it.visibility = View.INVISIBLE
            })
        }
    }

    private fun setSpinner()
    {
        usageAdapter = SpinnerAdapter(this,usageList,"alias",HiroUtils.Usage::class.java)
        usagesubAdapter = SpinnerAdapter(this,usagesubList,"alias",HiroUtils.Usage::class.java)
        bi.ui3IoForSpinner.adapter = usageAdapter
        bi.ui3IoForSpinnerSub.adapter = usagesubAdapter
        usageList.add(HiroUtils.Usage(0,"维修","维修用"))
        usageList.add(HiroUtils.Usage(1,"运输","运输器件"))
        usageList.add(HiroUtils.Usage(2,"暂存","临时存放"))
        usageList.add(HiroUtils.Usage(3,"废弃","已弃用"))
        usageAdapter.notifyDataSetChanged()
        usagesubList.add(HiroUtils.Usage(0,"维修","维修用"))
        usagesubList.add(HiroUtils.Usage(1,"运输","运输器件"))
        usagesubList.add(HiroUtils.Usage(2,"暂存","临时存放"))
        usagesubList.add(HiroUtils.Usage(3,"废弃","已弃用"))
        usagesubAdapter.notifyDataSetChanged()
    }

    private fun getExtraParameters()
    {
        maxNum = intent.getIntExtra("maxNum",0)
        usage.code = intent.getIntExtra("usage.code",0)
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

    private fun setExtraParameters()
    {
        bi.ui3IoTitle.text = name
        bi.ui3IoModel.text = model
        bi.ui3IoUid.text = uid
        bi.ui3IoShelf.text = "${shelf.main} - ${shelf.sub} (${shelf.alias})"
        bi.ui3IoFor.text = usage.alias
        bi.ui3IoLeft.text = String.format(getText(R.string.txt_dialog_item_count_left).toString(),maxNum)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        HiroUtils.setPopWinStatusBarColor(window, resources)
        super.onConfigurationChanged(newConfig)
    }
}