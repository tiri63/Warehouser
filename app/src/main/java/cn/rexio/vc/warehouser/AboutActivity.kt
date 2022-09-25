package cn.rexio.vc.warehouser

import HiroUtils
import android.app.Activity
import android.content.res.Configuration
import android.graphics.ImageDecoder
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import com.daimajia.androidanimations.library.Techniques


class AboutActivity : Activity() {
    private var isBouncing : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        HiroUtils.setPopWinStatusBarColor(window, resources)
        val img_gif = findViewById<ImageView>(R.id.ui_gabo_about)
        val source: ImageDecoder.Source = ImageDecoder.createSource(resources, R.drawable.ic_gabo)
        Thread{
            val drawable: Drawable = ImageDecoder.decodeDrawable(source)

            runOnUiThread {
                img_gif.setImageDrawable(drawable)

                if (drawable is Animatable) {
                    (img_gif.drawable as Animatable).start()
                }
            }

        }.start()
        img_gif.setOnClickListener {
            if(!isBouncing)
            {
                isBouncing = true
                HiroUtils.viewAnimation(img_gif,Techniques.Shake,200,{

                },{
                    isBouncing = false
                })
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        HiroUtils.setPopWinStatusBarColor(window, resources)
        super.onConfigurationChanged(newConfig)
    }
}