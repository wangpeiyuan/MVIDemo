package com.edgar.mvidemo.ui

import com.edgar.mvidemo.data.model.ResultResBean

/**
 * 页面状态
 * Created by wangpeiyuan on 2023/12/2.
 */
sealed class MainState {

    data object Idle : MainState()

    data object Loading : MainState()

    data class GetImageSuccess(val images: ResultResBean) : MainState()

    data class Error(val error: String) : MainState()
}