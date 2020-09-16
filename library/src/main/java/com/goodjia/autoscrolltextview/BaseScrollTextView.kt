package com.goodjia.autoscrolltextview

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.ViewSwitcher
import java.lang.ref.WeakReference
import java.util.*

/**
 * 父类：上下滚动
 *
 * @author haohao on 2017/9/21 下午 02:28
 * @version v1.0
 */
abstract class BaseScrollTextView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) : MarqueeSwitcher(context, attrs), ViewSwitcher.ViewFactory {
    private var itemClickListener: OnItemClickListener? = null

    /**
     * 当前显示Item的ID
     */
    private var currentId = -1
    private var textList: ArrayList<String>? = null
    private var myHandler: Handler? = null
    private fun init() {
        textList = ArrayList()
        myHandler = MyHandler(this)
        setFactory(this)
        inAnimation = AnimationUtils.loadAnimation(context, R.anim.push_up_in)
        outAnimation = AnimationUtils.loadAnimation(context, R.anim.push_up_out)
    }

    /**
     * 设置数据源
     */
    fun setTextList(titles: List<String>?) {
        textList?.clear()
        titles?.let { textList?.addAll(it) }
        currentId = -1
    }

    /**
     * 开始轮播
     */
    fun startAutoScroll() {
        myHandler?.sendEmptyMessage(FLAG_START_AUTO_SCROLL)
    }

    /**
     * 停止轮播
     */
    fun stopAutoScroll() {
        myHandler?.sendEmptyMessage(FLAG_STOP_AUTO_SCROLL)
        stopText()
    }

    /**
     * 设置点击事件监听
     */
    fun setOnItemClickListener(itemClickListener: OnItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    override fun makeView(): View {
        // FIXME: 2017/9/21 添加这层RelativeLayout是解决动画默认回到句首的问题
        val layout = RelativeLayout(context)
        val textView = makeTextView()
        textView.setOnClickListener {
            if (textList?.size ?: 0 > 0 && currentId != -1) {
                itemClickListener?.onItemClick(currentId % textList!!.size)
            }
        }
        textView.setMarqueeListener(object : IMarqueeListener {
            override fun onStart() {}
            override fun onFinish() {
                if (isStop) {
                    return
                }
                myHandler?.sendMessageDelayed(Message.obtain(handler, FLAG_AUTO_SCROLL), 1000)
            }
        })
        layout.addView(textView)
        return layout
    }

    private class MyHandler(autoScrollTextView: BaseScrollTextView) : Handler() {
        val textViewWeakReference: WeakReference<BaseScrollTextView> = WeakReference(autoScrollTextView)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val autoScrollTextView = textViewWeakReference.get()
            when (msg.what) {
                FLAG_START_AUTO_SCROLL -> {
                    isStop = false
                    if (autoScrollTextView?.textList?.size ?: 0 > 0) {
                        autoScrollTextView!!.currentId++
                        autoScrollTextView?.setText(autoScrollTextView.textList!![autoScrollTextView.currentId
                                % autoScrollTextView.textList!!.size])
                    }
                }
                FLAG_AUTO_SCROLL -> {
                    if (isStop) {
                        return
                    }
                    if (autoScrollTextView?.textList?.size ?: 0 > 0) {
                        autoScrollTextView!!.currentId++
                        autoScrollTextView?.setText(autoScrollTextView.textList!![autoScrollTextView.currentId
                                % autoScrollTextView.textList!!.size])
                    }
                }
                FLAG_STOP_AUTO_SCROLL -> {
                    isStop = true
                    autoScrollTextView?.myHandler?.removeMessages(FLAG_START_AUTO_SCROLL)
                    autoScrollTextView?.myHandler?.removeMessages(FLAG_AUTO_SCROLL)
                }
                else -> {
                }
            }
        }

    }

    /**
     * 轮播文本点击监听器
     */
    interface OnItemClickListener {
        /**
         * 点击回调
         *
         * @param position 当前点击ID
         */
        fun onItemClick(position: Int)
    }

    /**
     * 创建一个内部可横向滚动的textview
     */
    abstract fun makeTextView(): MarqueeTextView

    companion object {
        private const val FLAG_START_AUTO_SCROLL = 1000
        private const val FLAG_AUTO_SCROLL = 1001
        private const val FLAG_STOP_AUTO_SCROLL = 1002

        @Volatile
        private var isStop = false
    }

    init {
        init()
    }
}