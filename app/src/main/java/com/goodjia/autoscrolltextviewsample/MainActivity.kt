package com.goodjia.autoscrolltextviewsample

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.goodjia.autoscrolltextview.IMarqueeListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = MainActivity::class.simpleName
    }

    val randomColor
        get() = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
    private val contentList = listOf(
            "〔即時新聞／綜合報導〕日本官房長官菅義偉在14日當選自民黨總裁，預計將在今日經過國會指名成為日本第99任首相，至於安倍晉三卸任總理後，學者認為安倍晉三可望扮演日本「外交活棋」角色。\n",
            "海軍陸戰隊99旅膠艇7月3日在左營海域進行聯合登陸作戰操演時翻覆，當時造成2死1命危，其中原住民籍中士阿瑪勒．道卡度，受創以來用葉克膜續命。今早突然心律不整，病情急轉直下，家屬同意放棄急救，上午9時40分離開人世。",
            "藝人小鬼（黃鴻升）驟逝，引發眾人不捨，去年才透露要力拚45歲前，還完千萬房貸，未料發生憾事，經查，其北投住家去年1月才以總價4280萬元、單價65.9萬元，買下84坪住家，經查買方黃姓自然人，推測應是小鬼名下。\n",
            "綜合外媒報導，此次會談除了聚焦在投資協議的協商外，歐盟也針對新疆問題、《港版國安法》等人權議題，以及雙邊貿易的公平性向中國施壓，歐盟批評中國從鋼鐵產能過剩到竊取西方國家智慧財產的問題「違反了一連串的全球貿易原則」，但中國一概否認；另外，歐盟也認為中國市場的開放程度與歐盟的開放市場並不對等，指出在中國的「電信、IT、生技與醫療」等產業的阻礙太多，對歐洲業者來說相當不公平。",
            "轉換生活型態將大樓產品售出、「升級」成別墅，換取更宜居的居家生活型態。",
            "「這到底是什麼巫術？球竟然可以從這一邊打擊區飛到另一邊，而且球速還高達96 mph！」上週達比修先發對紅人吞下最近七連勝後的首敗，但這顆「噴射球」的超噁軌跡卻讓美國網友瞠目結舌！"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mtv.isInFinite = true
        mtv.text = contentList[Random.nextInt(contentList.size)].replace("\n", "")

        mtv.setMarqueeListener(object : IMarqueeListener {
            override fun onStart() {
                Log.d(TAG, "Marquee start")
            }

            override fun onLoopCompletion(count: Int) {
                Log.d(TAG, "Marquee LoopCompletion $count")
            }

            override fun onFinished() {
                Log.d(TAG, "Marquee Finished")
            }
        })

        btnStart.setOnClickListener { mtv.startScroll() }
        btnStop.setOnClickListener { mtv.stopScroll() }
        btnShow.setOnClickListener {
            val playTime = if (Random.nextBoolean()) null else Random.nextLong(20)
            val repeatTimes = if (Random.nextBoolean()) null else Random.nextInt(10)
            Log.d(TAG, "show play time $playTime, repeat time $repeatTimes")
            mtv.show(content = contentList[Random.nextInt(contentList.size)].replace("\n", ""),
                    speedScale = Random.nextFloat() * 3 + 0.3f,
                    textSize = Random.nextInt(50) + 15,
                    textColor = randomColor,
                    bgColor = randomColor,
                    letterSpacing = Random.nextFloat() + 0.1f, playTime = playTime, repeatTimes = repeatTimes)
        }
        btnDismiss.setOnClickListener { mtv.dismiss() }
    }
}