package com.goodjia.autoscrolltextviewsample

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.goodjia.autoscrolltextview.IMarqueeListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = MainActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val text = "「這到底是什麼巫術？球竟然可以從這一邊打擊區飛到另一邊，而且球速還高達96 mph！」上週達比修先發對紅人吞下最近七連勝後的首敗，但這顆「噴射球」的超噁軌跡卻讓美國網友瞠目結舌！"
        text.replace("\n", "")
        mtv.isInFinite = true
        mtv.text = text
        mtv.setMarqueeListener(object : IMarqueeListener {
            override fun onStart() {}
            override fun onFinish() {
//                mtv.postDelayed({ randomStartScroll() }, 3000)
            }
        })

        btnStart.setOnClickListener { randomStartScroll() }
        btnStop.setOnClickListener { mtv.stopScroll() }
        randomStartScroll()
    }

    private fun randomStartScroll() {
        val letterSpacing = Random.nextFloat() + 0.1f
        val scale = Random.nextFloat() * 3 + 0.3f
        val bgColor = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        val color = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        val textSize = Random.nextInt(50) + 15
        Log.d(TAG, "speed $scale, text size $textSize, letterSpacing $letterSpacing")
        mtv.speedScale = scale
        mtv.setBackgroundColor(bgColor)
        mtv.setTextColor(color)
        mtv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        mtv.letterSpacing = letterSpacing
        mtv.setText(mtv.text.toString())
        mtv.startScroll()
    }
}