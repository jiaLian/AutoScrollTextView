package com.goodjia.autoscrolltextview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
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
        val TAG = MarqueeTextView::class.simpleName
        const val DEFAULT_SPEED = 6
        const val DEFAULT_INFINITE_DELAY = 800L
    }

    private var currentScrollPos = 0
    private var playTime: Long? = null
    private var repeatTimes: Int? = null
    private val dismissRunnable = Runnable {
        dismiss()
        marqueeListener?.onFinished()
    }
    private var repeatCount = 0
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
    private val inFiniteRunnable = Runnable {
        startScroll(true)
    }
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
                repeatCount++
                marqueeListener?.onLoopCompletion(repeatCount)
                if (isInFinite) {
                    postDelayed(inFiniteRunnable, infiniteDelayMillis)
                }
                repeatTimes?.let {
                    if (repeatCount == it) {
                        dismiss()
                        marqueeListener?.onFinished()
                    }
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

    fun startScroll(keepRepeatValue: Boolean = false) {
        reset(keepRepeatValue)
        stopFuture()
        future = pool.scheduleAtFixedRate(task, 0, speed.toLong(), TimeUnit.MILLISECONDS)
        if (!keepRepeatValue) {
            playTime?.let {
                postDelayed(dismissRunnable, it * 1_000)
            }
        }
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

    fun reset(keepRepeatValue: Boolean = false) {
        flag = false
        isStop = false
        currentScrollPos = 0
        if (!keepRepeatValue) {
            repeatCount = 0
            removeCallbacks(dismissRunnable)
        }
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

    @JvmOverloads
    fun show(content: String? = null, speedScale: Float? = null, textSize: Int? = null, @ColorInt textColor: Int? = null, @ColorInt bgColor: Int? = null, letterSpacing: Float? = null, playTime: Long? = null, repeatTimes: Int? = null) {
        playTime?.let {
            this.playTime = it
            this.repeatTimes = null
        } ?: repeatTimes?.let {
            this.repeatTimes = it
            this.playTime = null
        } ?: setNull()
        speedScale?.let { this.speedScale = it }
        bgColor?.let { setBackgroundColor(it) }
        textColor?.let { setTextColor(it) }
        textSize?.let { setTextSize(TypedValue.COMPLEX_UNIT_SP, it.toFloat()) }
        letterSpacing?.let { this.letterSpacing = it }
        setText(content ?: text.toString())
        visibility = View.VISIBLE
        startScroll()
    }

    fun dismiss() {
        stopScroll()
        removeCallbacks(inFiniteRunnable)
        post { visibility = View.GONE }
    }

    private fun setNull() {
        repeatTimes = null
        playTime = null
    }

}