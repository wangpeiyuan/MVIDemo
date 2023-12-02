package com.edgar.mvidemo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edgar.mvidemo.data.repository.MainRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

/**
 *
 * Created by wangpeiyuan on 2023/12/2.
 */
class MainViewModel(private val repository: MainRepository) : ViewModel() {

    //意图管道，无限大
    val mainIntentChannel = Channel<MainIntent>(Channel.UNLIMITED)

    //状态数据流
    private val _state = MutableStateFlow<MainState>(MainState.Idle)
    val state: StateFlow<MainState> = _state

    init {
        viewModelScope.launch {
            mainIntentChannel.consumeAsFlow().collect() {
                when (it) {
                    is MainIntent.GetImages -> getImages()
                }
            }
        }
    }

    private fun getImages() {
        viewModelScope.launch {
            _state.value = MainState.Loading
            try {
                _state.value = MainState.GetImageSuccess(repository.getImageList())
            } catch (e: Exception) {
                _state.value = MainState.Error(e.localizedMessage ?: "Load image fail")
            }
        }
    }
}