package me.ryansimon.yelpfusion.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author Ryan Simon
 */
class ApiKeyInterceptor(private val apiKeyValue: String) : Interceptor {

    private val bearerHeaderKey = "Bearer"

    override fun intercept(chain: Interceptor.Chain?): Response {
        chain?.let {
            val original = chain.request()

            if (original.header(bearerHeaderKey) != null) {
                return chain.proceed(original)
            }

            val newRequest = original.newBuilder()
                    .addHeader(bearerHeaderKey, apiKeyValue)
                    .build()

            return chain.proceed(newRequest)
        }

        return Response.Builder().build()
    }
}