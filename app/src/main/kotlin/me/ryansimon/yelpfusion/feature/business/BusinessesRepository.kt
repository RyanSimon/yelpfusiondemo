package me.ryansimon.yelpfusion.feature.business

import me.ryansimon.yelpfusion.feature.business.BusinessFailure.*
import me.ryansimon.yelpfusion.network.Either
import me.ryansimon.yelpfusion.network.Either.Success
import me.ryansimon.yelpfusion.network.Either.Error
import me.ryansimon.yelpfusion.network.Failure
import me.ryansimon.yelpfusion.network.Failure.*
import me.ryansimon.yelpfusion.network.InternetConnectionHandler
import retrofit2.Call

/**
 * @author Ryan Simon
 */
class BusinessesRepository(private val businessesApi: BusinessesApi,
                           private val internetConnectionHandler: InternetConnectionHandler) {
    fun search(searchTerm: String,
               location: String,
               numResults: Int = 20,
               numResultsToSkip: Int = 0): Either<Failure, BusinessesResponse> {
        return when (internetConnectionHandler.isConnected) {
            true -> {
                try {
                    validateSearchParameters(numResults, numResultsToSkip)
                    request(businessesApi.search(searchTerm, location, numResults, numResultsToSkip))
                } catch (error: IllegalArgumentException) {
                    Error(SearchParametersAreInvalid())
                }
            }
            false -> Error(NoNetworkConnection())
        }
    }

    private fun validateSearchParameters(numResults: Int, numResultsToSkip: Int) {
        require(numResults in 1..50 && numResults + numResultsToSkip <= 1000 )
    }

    private fun <T> request(call: Call<T>): Either<Failure, T> {
        return try {
            val response = call.execute()
            when (response.isSuccessful && response.body() != null) {
                true -> Success(response.body()!!)
                false -> Error(ServerError())
            }
        } catch (exception: Throwable) {
            Error(ServerError())
        }
    }
}