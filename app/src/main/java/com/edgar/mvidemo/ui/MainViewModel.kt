package com.edgar.mvidemo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edgar.mvidemo.data.repository.MainRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 *
 * Created by wangpeiyuan on 2023/12/2.
 */
class MainViewModel(private val repository: MainRepository) : ViewModel() {

    //意图管道，容量为 64，满了会挂起 send
    val mainIntentChannel = Channel<MainIntent>(Channel.BUFFERED)

    //状态数据流
    private val _uiState = MutableStateFlow<MainState>(MainState.Idle)
    val uiState: StateFlow<MainState> = _uiState.asStateFlow()

    //一次性的状态事件 如 toast
    private val _effect = MutableSharedFlow<MainEffect>()
    val effect = _effect.asSharedFlow()

    init {
        // FIXME: 这里感觉用方法来的方便
        viewModelScope.launch {
            mainIntentChannel.consumeAsFlow().collect {
                when (it) {
                    is MainIntent.GetImages -> getImages()
                }
            }
        }
    }

    private fun getImages() {
        viewModelScope.launch {
            repository.getImageList()
                .onStart {
                    _uiState.value = MainState.Loading
                }
                .catch { ex ->
                    _uiState.value = MainState.Error(ex.localizedMessage ?: "Load image fail")
                }
                .collect { result ->
                    _uiState.value = MainState.GetImageSuccess(result)
                }
        }
    }
}