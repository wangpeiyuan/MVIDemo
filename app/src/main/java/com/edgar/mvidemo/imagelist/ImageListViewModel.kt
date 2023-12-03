package com.edgar.mvidemo.imagelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edgar.mvidemo.data.repository.ImageRepository
import com.edgar.mvidemo.network.RetrofitUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 *
 * Created by wangpeiyuan on 2023/12/3.
 */
class ImageListViewModel() : ViewModel() {

    //状态数据流
    private val _state = MutableStateFlow<ImageListState>(ImageListState.Idle)
    val state: StateFlow<ImageListState> = _state

    private val repository: ImageRepository by lazy {
        ImageRepository(RetrofitUtils.apiService)
    }

    fun getImages() {
        viewModelScope.launch {
            _state.value = ImageListState.Loading
            try {
                _state.value = ImageListState.GetImageSuccess(repository.getImageList())
            } catch (e: Exception) {
                _state.value = ImageListState.Error(e.localizedMessage ?: "Load image fail")
            }
        }
    }
}