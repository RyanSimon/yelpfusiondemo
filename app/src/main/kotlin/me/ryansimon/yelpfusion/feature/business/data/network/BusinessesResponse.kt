package me.ryansimon.yelpfusion.feature.business.data.network

import me.ryansimon.yelpfusion.feature.business.domain.Business

data class BusinessesResponse(val businesses: List<Business>, val total: Int)
