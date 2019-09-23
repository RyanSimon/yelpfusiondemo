package me.ryansimon.yelpfusion.feature.business.data.network

import me.ryansimon.yelpfusion.feature.business.domain.BusinessReview

/**
 * @author Ryan Simon
 */
data class BusinessReviewsResponse(val reviews: List<BusinessReview>)