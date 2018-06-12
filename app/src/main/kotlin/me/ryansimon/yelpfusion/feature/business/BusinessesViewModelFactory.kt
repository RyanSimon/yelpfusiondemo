package me.ryansimon.yelpfusion.feature.business

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import me.ryansimon.yelpfusion.BuildConfig
import me.ryansimon.yelpfusion.network.ApiConfiguration
import me.ryansimon.yelpfusion.network.InternetConnectionHandler


/**
 * @author Ryan Simon
 */
class BusinessesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val apiConfiguration = ApiConfiguration(BuildConfig.YELP_API_KEY)
        return BusinessesViewModel(
                application,
                BusinessesRepository(
                        apiConfiguration.retrofit.create(BusinessesApi::class.java),
                        InternetConnectionHandler(application)
                )
        ) as T
    }
}