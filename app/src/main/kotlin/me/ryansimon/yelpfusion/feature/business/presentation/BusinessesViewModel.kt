package me.ryansimon.yelpfusion.feature.business.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ryansimon.yelpfusion.feature.business.domain.Business
import me.ryansimon.yelpfusion.core.network.Failure
import me.ryansimon.yelpfusion.feature.business.domain.GetBusinessesBySearch
import me.ryansimon.yelpfusion.feature.business.domain.GetBusinessesBySearch.Params

/**
 * @author Ryan Simon
 */
class BusinessesViewModel(application: Application,
                          private val getBusinessesBySearch: GetBusinessesBySearch) : AndroidViewModel(application) {

    private val numResults = 20
    private var numResultsToSkip = 0
    private val location = "San Francisco, CA"

    /**
     * Observables
     */
    private val _businessesObservable = MutableLiveData<List<Business>>()
    val businessesObservable: LiveData<List<Business>>
        get() = _businessesObservable

    fun userSubmittedPaginatedSearch(searchTerm: String, newSearch: Boolean = true) {
        when {
            newSearch -> {
                numResultsToSkip = 0
                _businessesObservable.value = emptyList()
            }
            else -> numResultsToSkip += numResults
        }

        userSubmittedSearch(searchTerm, location, numResults, numResultsToSkip)
    }

    private fun userSubmittedSearch(searchTerm: String,
                                    location: String,
                                    numResults: Int,
                                    numResultsToSkip: Int) {
        getBusinessesBySearch(
                viewModelScope,
                Params(searchTerm, location, numResults, numResultsToSkip)
        ) {
            it.either(::processFailure, ::processSuccess)
        }
    }

    private fun processSuccess(businesses: List<Business>) {
        _businessesObservable.value = mergeBusinessList(businesses)
    }

    private fun processFailure(failure: Failure) {
        Log.d("Error", failure::class.java.simpleName)
    }

    private fun mergeBusinessList(businesses: List<Business>): List<Business> {
        val mergedList = mutableListOf<Business>()
        val currentBusinesses = _businessesObservable.value

        currentBusinesses?.let {
            mergedList.addAll(it)
        }

        mergedList.addAll(businesses)

        return mergedList
    }
}