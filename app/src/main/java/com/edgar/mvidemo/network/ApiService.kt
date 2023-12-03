package com.edgar.mvidemo.network

import com.edgar.mvidemo.data.model.ResultResBean
import retrofit2.http.GET

/**
 *
 * Created by wangpeiyuan on 2023/12/2.
 */
interface ApiService {

    @GET("v1/vertical/vertical?limit=30&skip=180&adult=false&first=0&order=hot")
    suspend fun getImageList(): ResultResBean
}