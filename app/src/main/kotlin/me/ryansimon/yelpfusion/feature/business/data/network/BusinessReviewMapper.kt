package me.ryansimon.yelpfusion.feature.business.data.network

/**
 * @author Ryan Simon
 */
class BusinessReviewMapper {
    fun make(businessReviewsResponse: BusinessReviewsResponse) = businessReviewsResponse.reviews
}