package me.ryansimon.yelpfusion.feature.business.data

import me.ryansimon.yelpfusion.feature.business.data.network.BusinessMapper
import me.ryansimon.yelpfusion.feature.business.data.network.BusinessesApi
import me.ryansimon.yelpfusion.feature.business.domain.Business
import me.ryansimon.yelpfusion.feature.business.domain.BusinessFailure.*
import me.ryansimon.yelpfusion.feature.business.domain.BusinessRepository
import me.ryansimon.yelpfusion.core.functional.Either
import me.ryansimon.yelpfusion.core.functional.Either.Success
import me.ryansimon.yelpfusion.core.functional.Either.Error
import me.ryansimon.yelpfusion.core.network.Failure
import me.ryansimon.yelpfusion.core.network.Failure.*
import me.ryansimon.yelpfusion.core.network.InternetConnectionHandler
import retrofit2.Call

/**
 * @author Ryan Simon
 */
class BusinessRepositoryImpl(
        private val businessesApi: BusinessesApi,
        private val internetConnectionHandler: InternetConnectionHandler,
        private val businessMapper: BusinessMapper) : BusinessRepository {

    override fun search(searchTerm: String,
               location: String,
               numResults: Int,
               numResultsToSkip: Int): Either<Failure, List<Business>> {
        return when (internetConnectionHandler.isConnected) {
            true -> {
                try {
                    validateSearchParameters(numResults, numResultsToSkip)
                    request(businessesApi.search(searchTerm, location, numResults, numResultsToSkip)) {
                        businessMapper.make(it)
                    }
                } catch (error: IllegalArgumentException) {
                    Error(SearchParametersAreInvalid)
                }
            }
            false -> Error(NoNetworkConnection)
        }
    }

    private fun validateSearchParameters(numResults: Int, numResultsToSkip: Int) {
        require(numResults in 1..50 && numResults + numResultsToSkip <= 1000)
    }

    private fun <T, R> request(call: Call<T>, transform: (T) -> R): Either<Failure, R> {
        return try {
            val response = call.execute()
            when (response.body() != null) {
                true -> Success(transform(response.body()!!))
                false -> Error(ServerError)
            }
        } catch (exception: Throwable) {
            Error(ServerError)
        }
    }
}