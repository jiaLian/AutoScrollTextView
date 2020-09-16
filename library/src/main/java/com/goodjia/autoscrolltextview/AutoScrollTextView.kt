package com.goodjia.autoscrolltextview

import android.content.Context
import android.util.AttributeSet

/**
 * 上下滚动
 *
 * @author haohao on 2017/9/21 下午 02:28
 * @version v1.0
 */
class AutoScrollTextView : BaseScrollTextView {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    override fun makeTextView(): MarqueeTextView {
        return MarqueeTextView(context)
    }
}