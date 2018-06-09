package me.ryansimon.yelpfusion.feature.business

import me.ryansimon.yelpfusion.network.Either
import me.ryansimon.yelpfusion.network.Either.Error
import me.ryansimon.yelpfusion.network.Either.Success
import me.ryansimon.yelpfusion.network.Failure
import me.ryansimon.yelpfusion.network.Failure.*
import me.ryansimon.yelpfusion.network.InternetConnectionHandler

/**
 * @author Ryan Simon
 */
class BusinessesRepository(private val businessesApi: BusinessesApi,
                           private val internetConnectionHandler: InternetConnectionHandler) {
    fun search(searchTerm: String, location: String): Either<Failure, BusinessesResponse> {
        return when (internetConnectionHandler.isConnected) {
            true -> Success(businessesApi.search(searchTerm, location).execute().body())
            false -> Error(NoNetworkConnection())
        }
    }
}