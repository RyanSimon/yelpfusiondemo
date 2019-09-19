package me.ryansimon.yelpfusion.feature.business

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BusinessesApi {
    // Have to return Response wrapped Object because Retrofit doesn't support nullable types yet
    @GET("/v3/businesses/search")
    suspend fun search(@Query("term") searchTerm: String,
               @Query("location") location: String,
               @Query("limit") numResults: Int,
               @Query("offset") numResultsToSkip: Int): Response<BusinessesResponse>
}
