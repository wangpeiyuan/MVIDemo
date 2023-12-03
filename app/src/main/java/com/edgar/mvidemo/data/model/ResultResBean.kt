package com.edgar.mvidemo.data.model


import com.squareup.moshi.Json

data class ResultResBean(
    @Json(name = "code")
    val code: Int,
    @Json(name = "msg")
    val msg: String,
    @Json(name = "res")
    val resBean: ResBean
)