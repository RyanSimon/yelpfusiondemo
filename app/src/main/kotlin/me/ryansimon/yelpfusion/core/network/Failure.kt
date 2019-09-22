package me.ryansimon.yelpfusion.core.network

/**
 * @author Ryan Simon
 */
sealed class Failure {
    object NoNetworkConnection : Failure()
    object ServerError : Failure()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure : Failure()
}