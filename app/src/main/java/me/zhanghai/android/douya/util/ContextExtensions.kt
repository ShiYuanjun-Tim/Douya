/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Bundle
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.annotation.AttrRes
import androidx.annotation.BoolRes
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.annotation.IntegerRes
import androidx.annotation.InterpolatorRes
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.appcompat.widget.TintTypedArray
import androidx.core.content.res.ResourcesCompat
import me.zhanghai.android.douya.R

val Context.activity: Activity?
    get() {
        var context = this
        while (true) {
            when (context) {
                is Activity -> return context
                is ContextWrapper -> context = context.baseContext
                else -> return null
            }
        }
    }

fun Context.getBoolean(@BoolRes id: Int) = resources.getBoolean(id)

fun Context.getFloat(@DimenRes id: Int) = ResourcesCompat.getFloat(resources, id)

fun Context.getInteger(@IntegerRes id: Int) = resources.getInteger(id)

fun Context.getInterpolator(@InterpolatorRes id: Int) = AnimationUtils.loadInterpolator(this, id)

fun Context.getStringArray(@ArrayRes id: Int) = resources.getStringArray(id)

fun Context.getColorByAttr(@AttrRes attr: Int): Int = getColorStateListByAttr(attr).defaultColor

fun Context.getColorStateListByAttr(@AttrRes attr: Int): ColorStateList =
    obtainStyledAttributesCompat(attrs = intArrayOf(attr)).use { it.getColorStateList(0) }
        ?: attributeNotFound(attr)

fun Context.getDimensionByAttr(@AttrRes attr: Int): Float =
    obtainStyledAttributesCompat(attrs = intArrayOf(attr)).use { it.getDimension(0, 0f) }

fun Context.getDimensionPixelOffsetByAttr(@AttrRes attr: Int): Int =
    obtainStyledAttributesCompat(attrs = intArrayOf(attr)).use {
        it.getDimensionPixelOffset(0, 0)
    }

fun Context.getDimensionPixelSizeByAttr(@AttrRes attr: Int): Int =
    obtainStyledAttributesCompat(attrs = intArrayOf(attr)).use { it.getDimensionPixelSize(0, 0) }

fun Context.obtainStyledAttributesCompat(
    set: AttributeSet? = null,
    @StyleableRes attrs: IntArray,
    @StyleRes defStyleRes: Int = 0,
    @AttrRes defStyleAttr: Int = 0
): TintTypedArray =
    TintTypedArray.obtainStyledAttributes(this, set, attrs, defStyleAttr, defStyleRes)

private inline fun <R> TintTypedArray.use(block: (TintTypedArray) -> R): R =
    AutoCloseable { recycle() }.use { block(this) }

private fun attributeNotFound(@AttrRes attr: Int): Nothing =
    throw Resources.NotFoundException("Attribute resource ID #0x${Integer.toHexString(attr)}")

@Dimension
fun Context.dpToDimension(@Dimension(unit = Dimension.DP) dp: Float): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

@Dimension
fun Context.dpToDimension(@Dimension(unit = Dimension.DP) dp: Int) = dpToDimension(dp.toFloat())

@Dimension
fun Context.dpToDimensionPixelOffset(@Dimension(unit = Dimension.DP) dp: Float): Int =
    dpToDimension(dp).toInt()

@Dimension
fun Context.dpToDimensionPixelOffset(@Dimension(unit = Dimension.DP) dp: Int) =
    dpToDimensionPixelOffset(dp.toFloat())

@Dimension
fun Context.dpToDimensionPixelSize(@Dimension(unit = Dimension.DP) dp: Float): Int {
    val value = dpToDimension(dp)
    val size = (if (value >= 0) value + 0.5f else value - 0.5f).toInt()
    return when {
        size != 0 -> size
        value == 0f -> 0
        value > 0 -> 1
        else -> -1
    }
}

@Dimension
fun Context.dpToDimensionPixelSize(@Dimension(unit = Dimension.DP) dp: Int) =
    dpToDimensionPixelSize(dp.toFloat())

val Context.shortAnimTime: Long
    get() = getInteger(android.R.integer.config_shortAnimTime).toLong()

val Context.mediumAnimTime: Long
    get() = getInteger(android.R.integer.config_mediumAnimTime).toLong()

val Context.longAnimTime: Long
    get() = getInteger(android.R.integer.config_longAnimTime).toLong()

val Context.layoutInflater
    get() = LayoutInflater.from(this)

fun Context.showToast(textRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, textRes, duration).show()
}

fun Context.showToast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun Context.startActivitySafe(intent: Intent, options: Bundle? = null) {
    try {
        startActivity(intent, options)
    } catch (e: ActivityNotFoundException) {
        showToast(R.string.activity_not_found)
    }
}
