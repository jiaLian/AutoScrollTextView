package com.goodjia.autoscrolltextview

/**
 * 走马灯监听
 *
 * @author haohao on 2017/9/21 下午 03:38
 * @version v1.0
 */
interface IMarqueeListener {
    fun onStart()
    fun onLoopCompletion(count: Int)
    fun onFinished()
}