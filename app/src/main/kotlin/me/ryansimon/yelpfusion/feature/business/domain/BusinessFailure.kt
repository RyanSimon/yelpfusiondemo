package me.ryansimon.yelpfusion.feature.business.domain

import me.ryansimon.yelpfusion.core.network.Failure.FeatureFailure

/**
 * @author Ryan Simon
 */
class BusinessFailure {
    object SearchParametersAreInvalid: FeatureFailure()
}

