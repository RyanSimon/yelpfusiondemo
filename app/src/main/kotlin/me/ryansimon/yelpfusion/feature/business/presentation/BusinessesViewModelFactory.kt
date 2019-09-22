package me.ryansimon.yelpfusion.feature.business.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.ryansimon.yelpfusion.BuildConfig
import me.ryansimon.yelpfusion.feature.business.data.network.BusinessMapper
import me.ryansimon.yelpfusion.feature.business.data.BusinessRepositoryImpl
import me.ryansimon.yelpfusion.feature.business.data.network.BusinessesApi
import me.ryansimon.yelpfusion.core.network.ApiConfiguration
import me.ryansimon.yelpfusion.core.network.InternetConnectionHandler
import me.ryansimon.yelpfusion.feature.business.domain.GetBusinessesBySearch


/**
 * @author Ryan Simon
 */
class BusinessesViewModelFactory(private val application: Application) : ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val apiConfiguration = ApiConfiguration(BuildConfig.YELP_API_KEY)
        return BusinessesViewModel(
                application,
                GetBusinessesBySearch(
                        BusinessRepositoryImpl(
                                apiConfiguration.retrofit.create(BusinessesApi::class.java),
                                InternetConnectionHandler(application),
                                BusinessMapper()
                        )
                )
        ) as T
    }
}