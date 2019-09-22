package me.ryansimon.yelpfusion.feature.business

import me.ryansimon.yelpfusion.feature.business.data.network.BusinessesResponse
import me.ryansimon.yelpfusion.feature.business.domain.Business

/**
 * @author Ryan Simon
 */
const val VALID_SEARCH_TERM = "Italian"
const val VALID_LOCATION = "Irvine, CA"
const val NUM_RESULTS = 20
const val NUM_RESULTS_TO_SKIP = 0
val BUSINESS = Business("", "")
val BUSINESS_RESPONSE = BusinessesResponse(listOf(BUSINESS), 0)
