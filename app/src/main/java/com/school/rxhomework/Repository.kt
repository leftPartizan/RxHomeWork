package com.school.rxhomework

import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object Repository {

    fun getPosts() = NetworkSource.getPosts()

    object NetworkSource {
        private interface IPostApi {
            @GET("/posts")
            fun getPosts(): Single<Response<List<MainActivity.Adapter.Item>>>
        }

        private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        private val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

        private val postApi = retrofit.create(IPostApi::class.java)

        fun getPosts() = postApi.getPosts()
    }
}
