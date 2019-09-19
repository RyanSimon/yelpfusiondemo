package me.ryansimon.yelpfusion.feature.business

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ryansimon.yelpfusion.network.Failure

/**
 * @author Ryan Simon
 */
class BusinessesViewModel(application: Application,
                          private val businessesRepository: BusinessesRepository) : AndroidViewModel(application) {

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
        viewModelScope.launch {
            // Don't actually need withContext here, because Retrofit automatically adds it for all API calls
            withContext(Dispatchers.IO) {
                businessesRepository.search(searchTerm, location, numResults, numResultsToSkip)
            }.either(::processFailure, ::processSuccess)
        }
    }

    private fun processSuccess(businessesResponse: BusinessesResponse) {
        val newList = mutableListOf<Business>()
        val currentBusinesses = _businessesObservable.value
        currentBusinesses?.let {
            newList.addAll(it)
        }
        newList.addAll(businessesResponse.businesses)
        _businessesObservable.value = newList
    }

    private fun processFailure(failure: Failure) {
        Log.d("Error", failure::class.java.simpleName)
    }
}