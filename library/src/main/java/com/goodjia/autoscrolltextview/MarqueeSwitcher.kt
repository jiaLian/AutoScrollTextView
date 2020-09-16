package com.goodjia.autoscrolltextview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.ViewSwitcher
import com.goodjia.autoscrolltextview.MarqueeTextView

/**
 * MarqueeSwitcher [android.widget.TextSwitcher] t
 *
 * @author haohao on 2017/9/21 下午 03:57
 * @version v1.0
 */
open class MarqueeSwitcher : ViewSwitcher {
    /**
     * Creates a new empty TextSwitcher.
     *
     * @param context the application's environment
     */
    constructor(context: Context?) : super(context) {}

    /**
     * Creates a new empty TextSwitcher for the given context and with the
     * specified set attributes.
     *
     * @param context the application environment
     * @param attrs a collection of attributes
     */
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if child is not an instance of
     * [android.widget.TextView]
     */
    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        require(child is RelativeLayout) { "MarqueeSwitcher children must be instances of RelativeLayout" }
        super.addView(child, index, params)
    }

    /**
     * Sets the text of the next view and switches to the next view. This can
     * be used to animate the old text out and animate the next text in.
     *
     * @param text the new text to display
     */
    fun setText(text: String?) {
        val t = nextView
        t.setText(text)
        t.postStartScroll(1500)
        showNext()
    }

    fun setText(text: String?, iMarqueeListener: IMarqueeListener?) {
        val t = nextView
        t.setMarqueeListener(iMarqueeListener)
        t.setText(text)
        t.postStartScroll(1500)
        showNext()
    }

    fun stopText() {
        val t = nextView
        t?.stopScroll()
        val t1 = currentView
        if (t != null) t1.stopScroll()
    }

    override fun getCurrentView(): MarqueeTextView {
        return (super.getCurrentView() as RelativeLayout).getChildAt(0) as MarqueeTextView
    }

    override fun getNextView(): MarqueeTextView {
        return (super.getNextView() as RelativeLayout).getChildAt(0) as MarqueeTextView
    }

    override fun getAccessibilityClassName(): CharSequence {
        return MarqueeTextView::class.java.name
    }
}