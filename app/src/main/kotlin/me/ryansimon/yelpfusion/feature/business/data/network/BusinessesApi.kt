package me.ryansimon.yelpfusion.feature.business.data.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BusinessesApi {
    /**
     * @param searchTerm
     * @param location
     * @param numResults; API defaults to 20
     * @param numResultsToSkip; API defaults to 0
     */
    @GET("/v3/businesses/search")
    fun search(@Query("term") searchTerm: String,
               @Query("location") location: String,
               @Query("limit") numResults: Int = 20,
               @Query("offset") numResultsToSkip: Int = 0): Call<BusinessesResponse>

    @GET("v3/businesses/{id}/reviews")
    fun getBusinessReviews(@Path("id") businessId: String): Call<BusinessReviewsResponse>
}
