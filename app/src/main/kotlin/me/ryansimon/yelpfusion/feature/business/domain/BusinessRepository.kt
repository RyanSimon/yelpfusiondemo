package me.ryansimon.yelpfusion.feature.business.domain

import me.ryansimon.yelpfusion.core.functional.Either
import me.ryansimon.yelpfusion.core.network.Failure

/**
 * @author Ryan Simon
 */
interface BusinessRepository {
    fun search(searchTerm: String,
               location: String,
               numResults: Int,
               numResultsToSkip: Int): Either<Failure, List<Business>>
}