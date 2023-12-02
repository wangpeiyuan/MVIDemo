package com.edgar.mvidemo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.edgar.mvidemo.data.repository.MainRepository
import com.edgar.mvidemo.network.ApiService

/**
 *
 * Created by wangpeiyuan on 2023/12/2.
 */
class ViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(MainRepository(apiService)) as T
        }
        throw IllegalArgumentException("UnKnown class")
    }
}