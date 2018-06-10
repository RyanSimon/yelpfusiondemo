package me.ryansimon.yelpfusion.feature.business

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BusinessesApi {
    @GET("/v3/businesses/search")
    fun search(@Query("term") searchTerm: String,
               @Query("location") location: String,
               @Query("limit") numResults: Int,
               @Query("offset") numResultsToSkip: Int): Call<BusinessesResponse>
}
