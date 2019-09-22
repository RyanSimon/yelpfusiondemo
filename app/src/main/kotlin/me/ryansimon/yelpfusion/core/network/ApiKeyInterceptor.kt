package me.ryansimon.yelpfusion.core.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author Ryan Simon
 */
class ApiKeyInterceptor(private val apiKeyValue: String) : Interceptor {

    private val bearerHeaderValuePrefix = "Bearer "
    private val authHeaderKey = "Authorization"

    override fun intercept(chain: Interceptor.Chain?): Response {
        chain?.let {
            val original = chain.request()

            if (original.header(authHeaderKey) != null) {
                return chain.proceed(original)
            }

            val newRequest = original.newBuilder()
                    .addHeader(authHeaderKey, bearerHeaderValuePrefix + apiKeyValue)
                    .build()

            return chain.proceed(newRequest)
        }

        return Response.Builder().build()
    }
}