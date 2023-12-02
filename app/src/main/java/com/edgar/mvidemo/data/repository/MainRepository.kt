package com.edgar.mvidemo.data.repository

import com.edgar.mvidemo.network.ApiService
import kotlinx.coroutines.flow.flow

/**
 *
 * Created by wangpeiyuan on 2023/12/2.
 */
class MainRepository(private val apiService: ApiService) {
    suspend fun getImageList() = flow {
        emit(apiService.getImageList())
    }
}