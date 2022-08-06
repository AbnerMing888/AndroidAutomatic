@file:Suppress("unused", "NOTHING_TO_INLINE")

package com.abner.automatic.util

import android.content.Context


/**
 *AUTHOR:AbnerMing
 *DATE:2021/11/1
 *INTRODUCE:获取手机宽和高，以及dp,sp和px进行转换
 */
/**
 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
 */

inline fun Context.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
 */
inline fun Context.px2dp(pxValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

inline fun Context.px2sp(pxValue: Float): Int {
    val fontScale = resources.displayMetrics.scaledDensity
    return (pxValue / fontScale + 0.5f).toInt()
}

inline fun Context.sp2px(spValue: Float): Int {
    val fontScale: Float = resources.displayMetrics.scaledDensity
    return (spValue * fontScale + 0.5f).toInt()
}

/**
 * AUTHOR:AbnerMing
 * INTRODUCE:获取屏幕的高
 */
inline fun Context.getScreenHeight(): Int {
    return resources.displayMetrics.heightPixels
}

/**
 * AUTHOR:AbnerMing
 * INTRODUCE:获取屏幕的宽
*/
inline fun Context.getScreenWidth(): Int {
    return resources.displayMetrics.widthPixels
}