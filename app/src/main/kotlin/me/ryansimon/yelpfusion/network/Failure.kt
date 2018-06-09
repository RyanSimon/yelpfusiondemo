package me.ryansimon.yelpfusion.network

/**
 * @author Ryan Simon
 */
sealed class Failure {
    class NoNetworkConnection : Failure()
    class ServerError : Failure()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure : Failure()
}