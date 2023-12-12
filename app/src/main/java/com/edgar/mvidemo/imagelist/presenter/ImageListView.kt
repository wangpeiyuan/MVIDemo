package com.edgar.mvidemo.imagelist.presenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.edgar.mvidemo.data.repository.ImageRepository
import com.edgar.mvidemo.imagelist.ImageListState
import com.edgar.mvidemo.imagelist.ImageLoadInitScreen
import com.edgar.mvidemo.imagelist.ImageProgressScreen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow

/**
 *
 * Created by wangpeiyuan on 2023/12/12.
 */
@Composable
fun ImageListView(
    modifier: Modifier = Modifier,
    repository: ImageRepository,
) {
    val channel = remember { Channel<Event>() }
    val flow = remember(channel) { channel.consumeAsFlow() }
    val state = ImageListPresenter(flow, repository)
    ImageListView(state, modifier) { channel.trySend(Event.GetImageEvent) }
}

@Composable
fun ImageListView(
    state: ImageListState,
    modifier: Modifier = Modifier,
    loadData: () -> Unit
) {
    when (state) {
        is ImageListState.Idle -> {
            ImageLoadInitScreen(false, modifier, loadData)
        }

        is ImageListState.Loading -> {
            ImageProgressScreen(modifier)
        }

        is ImageListState.GetImageSuccess -> {
            com.edgar.mvidemo.imagelist.ImageListScreen(
                state.images.resBean.imageInfoBean,
                modifier
            )
        }

        is ImageListState.Error -> {
            ImageLoadInitScreen(true, modifier, loadData)
        }
    }
}