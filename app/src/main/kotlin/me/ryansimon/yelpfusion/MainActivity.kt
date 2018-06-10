package me.ryansimon.yelpfusion

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import me.ryansimon.yelpfusion.feature.business.*
import me.ryansimon.yelpfusion.network.ApiConfiguration
import me.ryansimon.yelpfusion.network.InternetConnectionHandler
import android.support.v7.widget.RecyclerView
import io.reactivex.disposables.Disposable
import me.ryansimon.yelpfusion.feature.business.EndlessRecyclerViewScrollListener



class MainActivity : AppCompatActivity() {

    private val apiConfiguration = ApiConfiguration(BuildConfig.YELP_API_KEY)
    private val businessesRepository = BusinessesRepository(
            apiConfiguration.retrofit.create(BusinessesApi::class.java),
            InternetConnectionHandler(this)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBusinessList()
        performSearch("pizza", "Irvine, CA", business_list_view)
    }

    private fun setupBusinessList() {
        val businessListView = business_list_view
        val scrollListener = object : EndlessRecyclerViewScrollListener(businessListView.layoutManager as GridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                performSearch("pizza", "Irvine, CA", businessListView)
            }
        }
        businessListView.setHasFixedSize(true)
        businessListView.addOnScrollListener(scrollListener)
        businessListView.adapter = BusinessesAdapter()
    }

    private fun performSearch(searchTerm: String,
                              location: String,
                              businessListView: RecyclerView): Disposable {
        val callable = { businessesRepository.search(searchTerm, location) }
        return Single.fromCallable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess({
                    success -> success.either(
                        {},
                        { businessesResponse -> (businessListView.adapter as BusinessesAdapter).addBusinesses(businessesResponse.businesses) }
                )
                })
                .doOnError({ error -> Log.d("Error", error.message)})
                .subscribe()
    }
}
