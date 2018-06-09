package me.ryansimon.yelpfusion.feature.business

import me.ryansimon.yelpfusion.network.Either
import me.ryansimon.yelpfusion.network.Either.Success
import me.ryansimon.yelpfusion.network.Failure

/**
 * @author Ryan Simon
 */
class BusinessesRepository(private val businessesApi: BusinessesApi) {
    fun search(searchTerm: String, location: String): Either<Failure, BusinessesResponse> {
        return Success(businessesApi.search(searchTerm, location).execute().body())
    }
}