package me.ryansimon.yelpfusion.feature.business

import me.ryansimon.yelpfusion.network.Either
import me.ryansimon.yelpfusion.network.Either.Error
import me.ryansimon.yelpfusion.network.Either.Success
import me.ryansimon.yelpfusion.network.Failure
import me.ryansimon.yelpfusion.network.Failure.*
import me.ryansimon.yelpfusion.network.InternetConnectionHandler
import retrofit2.Call

/**
 * @author Ryan Simon
 */
class BusinessesRepository(private val businessesApi: BusinessesApi,
                           private val internetConnectionHandler: InternetConnectionHandler) {
    fun search(searchTerm: String, location: String): Either<Failure, BusinessesResponse> {
        return when (internetConnectionHandler.isConnected) {
            true -> request(businessesApi.search(searchTerm, location))
            false -> Error(NoNetworkConnection())
        }
    }

    private fun <T> request(call: Call<T>): Either<Failure, T> {
        val response = call.execute()
        return Success(response.body())
    }
}