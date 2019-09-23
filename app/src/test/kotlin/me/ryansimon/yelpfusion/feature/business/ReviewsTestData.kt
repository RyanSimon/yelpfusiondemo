package me.ryansimon.yelpfusion.feature.business

import me.ryansimon.yelpfusion.feature.business.data.network.BusinessReviewsResponse
import me.ryansimon.yelpfusion.feature.business.domain.BusinessReview

/**
 * @author Ryan Simon
 */
const val BUSINESS_ID = "someBusinessId"
val BUSINESS_REVIEW = BusinessReview(5, "")
val BUSINESS_REVIEW_RESPONSE = BusinessReviewsResponse(listOf(BUSINESS_REVIEW))
