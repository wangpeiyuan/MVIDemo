package com.edgar.mvidemo.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 *
 * Created by wangpeiyuan on 2023/12/2.
 */
object RetrofitUtils {

    private const val BASE_URL = "http://service.picasso.adesk.com/"

    /**
     * 通过Moshi 将JSON转为为 Kotlin 的Data class
     */
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    /**
     * 构建Retrofit
     */
    private fun getRetrofit() = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    /**
     * 创建Api网络请求服务
     */
    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
}