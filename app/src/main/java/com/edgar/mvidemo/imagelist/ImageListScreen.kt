package com.edgar.mvidemo.imagelist

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.RequestBuilderTransform
import com.bumptech.glide.integration.compose.placeholder
import com.edgar.mvidemo.R
import com.edgar.mvidemo.data.model.ImageInfoBean

/**
 *
 * Created by wangpeiyuan on 2023/12/3.
 */
@Composable
fun ImageListScreen(
    modifier: Modifier = Modifier,
    viewModel: ImageListViewModel = viewModel(
        modelClass = ImageListViewModel::class.java
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ImageListScreen(state, modifier, viewModel::getImages)
}

@Composable
fun ImageListScreen(
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
            ImageListScreen(state.images.resBean.imageInfoBean, modifier)
        }

        is ImageListState.Error -> {
            ImageLoadInitScreen(true, modifier, loadData)
        }
    }
}

@Composable
fun ImageLoadInitScreen(isRetry: Boolean, modifier: Modifier = Modifier, loadData: () -> Unit) {
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Button(
            onClick = loadData,
            modifier = Modifier
                .height(40.dp)
        ) {
            Text(text = stringResource(id = if (isRetry) R.string.retry else R.string.load_image))
        }
    }

}

@Composable
fun ImageProgressScreen(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp)
        )
    }
}

@Composable
fun ImageListScreen(images: List<ImageInfoBean>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.imePadding(),
        contentPadding = PaddingValues(
            horizontal = dimensionResource(id = R.dimen.card_side_margin),
            vertical = dimensionResource(id = R.dimen.card_side_margin)
        )
    ) {
        items(items = images, key = { it.id }) { image ->
            ImageListItem(image)
        }
    }
}

@Composable
fun ImageListItem(image: ImageInfoBean, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = modifier
            .padding(horizontal = dimensionResource(id = R.dimen.card_side_margin))
            .padding(bottom = dimensionResource(id = R.dimen.card_side_margin))
    ) {
        Image(
            model = image.img,
            contentDescription = "",
            Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.item_image_height)),
            contentScale = ContentScale.Crop
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Image(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    requestBuilderTransform: RequestBuilderTransform<Drawable> = { it }
) {
    if (LocalInspectionMode.current) {
        Box(modifier = modifier.background(Color.Magenta))
        return
    }
    GlideImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        requestBuilderTransform = requestBuilderTransform,
        loading = placeholder {
            Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(Modifier.size(40.dp))
            }
        }
    )
}

@Preview
@Composable
private fun ImageLoadInitScreenPreview() {
    ImageLoadInitScreen(false) {

    }
}

@Preview
@Composable
private fun ImageProgressScreenPreview() {
    ImageProgressScreen()
}

@Preview
@Composable
private fun ImageListScreenPreview(@PreviewParameter(ImageListPreviewParamProvider::class) images: List<ImageInfoBean>) {
    ImageListScreen(images)
}

private class ImageListPreviewParamProvider : PreviewParameterProvider<List<ImageInfoBean>> {
    override val values: Sequence<List<ImageInfoBean>> =
        sequenceOf(
            emptyList(),
            listOf(
                ImageInfoBean(
                    1, emptyList(), false, "", 1, "1", "",
                    1, "", 2, "", "", "", emptyList(),
                    "", emptyList(), 1, "", false
                ),
                ImageInfoBean(
                    2, emptyList(), true, "", 2, "2", "",
                    2, "", 6, "", "", "", emptyList(),
                    "", emptyList(), 2, "", false
                ),
                ImageInfoBean(
                    3, emptyList(), false, "", 1, "3", "",
                    1, "", 6, "", "", "", emptyList(),
                    "", emptyList(), 1, "", false
                )
            )
        )
}