package me.ryansimon.yelpfusion.feature.business

import me.ryansimon.yelpfusion.feature.business.BusinessesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BusinessesApi {
    @GET("/v3/businesses/search")
    fun search(@Query("term") searchTerm: String,
               @Query("location") location: String): Call<BusinessesResponse>
}
