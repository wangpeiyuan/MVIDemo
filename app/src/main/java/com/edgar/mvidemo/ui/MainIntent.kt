package com.edgar.mvidemo.ui

/**
 * 页面意图
 * Created by wangpeiyuan on 2023/12/2.
 */
sealed class MainIntent {
    data object GetImages : MainIntent()
}