package com.edgar.mvidemo.imagelist

import com.edgar.mvidemo.data.model.ResultResBean

/**
 *
 * Created by wangpeiyuan on 2023/12/3.
 */
sealed interface ImageListState {
    data object Idle : ImageListState

    data object Loading : ImageListState

    data class GetImageSuccess(val images: ResultResBean) : ImageListState

    data class Error(val error: String) : ImageListState
}