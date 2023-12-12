package com.edgar.mvidemo.imagelist.presenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.edgar.mvidemo.data.model.ResultResBean
import com.edgar.mvidemo.data.repository.ImageRepository
import com.edgar.mvidemo.imagelist.ImageListState
import kotlinx.coroutines.flow.Flow

/**
 *
 * Created by wangpeiyuan on 2023/12/12.
 */
@Composable
fun ImageListPresenter(events: Flow<Event>, repository: ImageRepository): ImageListState {
    var images: ResultResBean? by remember { mutableStateOf(null) }
    var loading by remember { mutableStateOf(false) }
    var errorMsg: String? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is Event.GetImageEvent -> {
                    loading = true
                    try {
                        images = repository.getImageList()
                        loading = false
                    } catch (e: Exception) {
                        loading = false
                        errorMsg = e.localizedMessage ?: "Load image fail"
                    }
                }
            }
        }

    }

    return if (loading) {
        ImageListState.Loading
    } else if (images != null) {
        ImageListState.GetImageSuccess(images!!)
    } else if (errorMsg != null) {
        ImageListState.Error(errorMsg!!)
    } else {
        ImageListState.Idle
    }
}

sealed interface Event {
    data object GetImageEvent : Event
}