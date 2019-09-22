package me.ryansimon.yelpfusion.feature.business.domain

import me.ryansimon.yelpfusion.core.interactor.UseCase
import me.ryansimon.yelpfusion.core.functional.Either
import me.ryansimon.yelpfusion.core.network.Failure

/**
 * @author Ryan Simon
 */
class GetBusinessesBySearch(
        private val businessesRepository: BusinessRepository
) : UseCase<List<Business>, GetBusinessesBySearch.Params>() {

    override suspend fun run(params: Params): Either<Failure, List<Business>> {
        return businessesRepository.search(
                params.searchTerm,
                params.location,
                params.numResults,
                params.numResultsToSkip
        )
    }

    data class Params(
            val searchTerm: String,
            val location: String,
            val numResults: Int,
            val numResultsToSkip: Int
    )
}
