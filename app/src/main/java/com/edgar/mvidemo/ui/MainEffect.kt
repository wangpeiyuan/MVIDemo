package com.edgar.mvidemo.ui

/**
 *
 * Created by wangpeiyuan on 2023/12/2.
 */
sealed class MainEffect {
    data class ToastEffect(val msg: String) : MainEffect()
}