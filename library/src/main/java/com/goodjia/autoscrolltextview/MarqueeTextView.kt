package com.goodjia.autoscrolltextview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * 跑马灯效果TextView
 *
 * @author haohao on 2017/9/21 下午 02:33
 * @version v1.0
 */
class MarqueeTextView : TextView {
    companion object {
        const val DEFAULT_SPEED = 6
        const val DEFAULT_INFINITE_DELAY = 800L
    }

    private var currentScrollPos = 0

    var baseSpeed = DEFAULT_SPEED
    var infiniteDelayMillis = DEFAULT_INFINITE_DELAY

    //速度
    private var speed = baseSpeed

    var speedScale: Float = 1f
        set(value) {
            speed = (baseSpeed / value).toInt()
            field = value
        }

    // 文字宽度
    private var textWidth = -1

    //是否计算了宽度
    @Volatile
    private var isMeasured = false

    //是否完成移动
    @Volatile
    private var flag = false

    //是否停止移动
    @Volatile
    private var isStop = false
    var isInFinite = false
    private var marqueeListener: IMarqueeListener? = null
    private var future: Future<*>? = null
    private var pool = Executors.newScheduledThreadPool(1)
    private val task: TimerTask? = object : TimerTask() {
        override fun run() {
            if (textWidth == -1) {
                postInvalidate()
                return
            }
            if (isStop) {
                return
            }
            if (!flag && currentScrollPos >= textWidth - width) {
                //currentScrollPos = -getWidth();
                this.cancel()
                flag = true
                marqueeListener?.onFinish()
                if (isInFinite) {
                    postDelayed({ startScroll() }, infiniteDelayMillis)
                }
            }
            if (!flag) {
                currentScrollPos += 1
                scrollTo(currentScrollPos, 0)
            }
        }
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setSingleLine()
    }

    fun startScroll() {
        reset()
        stopFuture()
        future = pool.scheduleAtFixedRate(task, 0, speed.toLong(), TimeUnit.MILLISECONDS)
    }

    fun postStartScroll(delay: Int) {
        reset()
        stopFuture()
        future = pool.scheduleAtFixedRate(task, delay.toLong(), speed.toLong(), TimeUnit.MILLISECONDS)
    }

    fun stopScroll() {
        isStop = true
        stopFuture()
    }

    fun setMarqueeListener(marqueeListener: IMarqueeListener?) {
        this.marqueeListener = marqueeListener
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isMeasured) {
            getTextWidth()
            isMeasured = true
        }
    }

    private fun getTextWidth() {
        val paint: Paint = this.paint
        val str = this.text.toString()
        if (TextUtils.isEmpty(str)) {
            textWidth = 0
        }
        textWidth = paint.measureText(str).toInt()
    }

    fun reset() {
        flag = false
        isStop = false
        currentScrollPos = 0
        scrollTo(currentScrollPos, 0)
    }

    fun setText(str: String?) {
        super.setText(str)
        isMeasured = false
        invalidate()
    }

    @Synchronized
    private fun stopFuture() {
        if (future != null && !future!!.isCancelled) {
            future?.cancel(true)
        }
        task?.cancel()
    }
}