package me.ryansimon.yelpfusion.feature.business

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * @author Ryan Simon
 */
class BusinessesViewModel(application: Application,
                          private val businessesRepository: BusinessesRepository)
    : AndroidViewModel(application) {

    private val numResults = 20
    private var numResultsToSkip = 0
    private val location = "San Francisco, CA"
    private var currentSearchDisposable: Disposable? = null

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
        val callable = { businessesRepository.search(searchTerm, location, numResults, numResultsToSkip) }
        currentSearchDisposable = Single.fromCallable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess({
                    success -> success.either(
                        { Log.d("Error", "uh oh") },
                        { businessesResponse ->
                            val newList = mutableListOf<Business>()
                            val currentBusinesses = _businessesObservable.value
                            currentBusinesses?.let {
                                newList.addAll(it)
                            }
                            newList.addAll(businessesResponse.businesses)
                            _businessesObservable.setValue(newList)
                        }
                )
                })
                .doOnError({ error -> Log.d("Error", error.message)})
                .subscribe()
    }

    override fun onCleared() {
        currentSearchDisposable?.dispose()
        super.onCleared()
    }
}