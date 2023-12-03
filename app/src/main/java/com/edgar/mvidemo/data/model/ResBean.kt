package com.edgar.mvidemo.data.model


import com.squareup.moshi.Json

data class ResBean(
    @Json(name = "vertical")
    val imageInfoBean: List<ImageInfoBean>
)