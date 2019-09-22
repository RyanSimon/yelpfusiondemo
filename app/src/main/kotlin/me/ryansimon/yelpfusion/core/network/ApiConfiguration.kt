package me.ryansimon.yelpfusion.core.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author Ryan Simon
 */
class ApiConfiguration(apiKey: String) {

    private val apiKeyInterceptor = ApiKeyInterceptor(apiKey)
    private val apiUrl = "https://api.yelp.com/"

    val retrofit = getRetrofit(apiUrl, apiKeyInterceptor)

    private fun getRetrofit(baseUrl: String, vararg interceptors: Interceptor): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient(*interceptors))
                .build()
    }

    private fun getHttpClient(vararg interceptors: Interceptor): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()

        interceptors.forEach { interceptor -> httpClientBuilder.addInterceptor(interceptor) }

        return httpClientBuilder.build()
    }
}