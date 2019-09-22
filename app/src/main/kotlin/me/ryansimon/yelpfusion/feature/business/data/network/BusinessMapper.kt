package me.ryansimon.yelpfusion.feature.business.data.network

/**
 * @author Ryan Simon
 */
class BusinessMapper {
    fun make(businessesResponse: BusinessesResponse) = businessesResponse.businesses
}