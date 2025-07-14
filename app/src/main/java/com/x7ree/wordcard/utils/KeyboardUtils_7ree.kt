package com.x7ree.wordcard.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * 显示软键盘并聚焦，带有延迟以确保成功率
 */
fun View.showKeyboardWithDelay_7ree(delay: Long = 200) {
    this.requestFocus()
    this.postDelayed({
        val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }, delay)
}

/**
 * 从一个视图隐藏软键盘
 */
fun View.hideKeyboard_7ree() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

/**
 * 从一个活动隐藏软键盘
 */
fun Activity.hideKeyboard_7ree() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = currentFocus
    if (view == null) {
        // 如果没有焦点视图，创建一个新的临时视图来获取窗口token
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}