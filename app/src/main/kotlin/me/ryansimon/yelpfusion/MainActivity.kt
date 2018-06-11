package me.ryansimon.yelpfusion

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.CursorAdapter
import android.support.v4.widget.SimpleCursorAdapter
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
import android.support.v7.widget.SearchView
import me.ryansimon.yelpfusion.extension.hideKeyboard
import android.provider.BaseColumns
import android.database.MatrixCursor


/**
 * @author Ryan Simon
 */
class MainActivity : AppCompatActivity() {

    private val suggestions = mutableListOf<String>()
    private val suggestionsId = "suggestions"
    private val apiConfiguration = ApiConfiguration(BuildConfig.YELP_API_KEY)
    private val businessesRepository = BusinessesRepository(
            apiConfiguration.retrofit.create(BusinessesApi::class.java),
            InternetConnectionHandler(this)
    )
    private val numResults = 50
    private var numResultsToSkip = 0
    private val searchLocation = "San Francisco, CA"
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setupBusinessList()
        setupSearchView()
    }

    private fun setupSearchView() {
        searchView = search_view
        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val adapter = business_list_view.adapter as BusinessesAdapter
                adapter.clear()

                if(!suggestions.contains(query)) {
                    suggestions.add(query)
                }

                performSearch(query, searchLocation, businessListView = business_list_view)

                hideKeyboard()

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                populateAdapter(newText)
                return false
            }
        })
        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionClick(position: Int): Boolean {
                val suggestion = searchView.suggestionsAdapter.getItem(position) as? String
                suggestion?.let {
                    performSearch(it, searchLocation, businessListView = business_list_view)
                }

                return true
            }

            override fun onSuggestionSelect(position: Int): Boolean {
                val suggestion = searchView.suggestionsAdapter.getItem(position) as? String
                suggestion?.let {
                    performSearch(it, searchLocation, businessListView = business_list_view)
                }

                return true
            }
        })
        val to = intArrayOf(android.R.id.text1)
        val from = arrayOf(suggestionsId)
        searchView.suggestionsAdapter = SimpleCursorAdapter(
                this,
                android.R.layout.simple_selectable_list_item,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
        searchView.setQuery("pizza", true)
    }

    private fun populateAdapter(query: String) {
        val matrixCursor = MatrixCursor(arrayOf(BaseColumns._ID, suggestionsId))
        (0 until suggestions.size)
                .filter { suggestions[it].toLowerCase().startsWith(query.toLowerCase()) }
                .forEach { matrixCursor.addRow(arrayOf(it, suggestions[it])) }

        searchView.suggestionsAdapter.changeCursor(matrixCursor)
    }

    private fun setupBusinessList() {
        val businessListView = business_list_view
        val scrollListener = object : EndlessRecyclerViewScrollListener(businessListView.layoutManager as GridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                numResultsToSkip += numResults
                performSearch(searchView.query.toString(), searchLocation, numResultsToSkip, businessListView)
            }
        }
        businessListView.setHasFixedSize(true)
        businessListView.addOnScrollListener(scrollListener)
        businessListView.adapter = BusinessesAdapter()
    }

    private fun performSearch(searchTerm: String,
                              location: String,
                              numResultsToSkip: Int = this.numResultsToSkip,
                              businessListView: RecyclerView): Disposable {
        val callable = { businessesRepository.search(searchTerm, location, numResults, numResultsToSkip) }
        return Single.fromCallable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess({
                    success -> success.either(
                        { Log.d("Error", "uh oh") },
                        { businessesResponse -> (businessListView.adapter as BusinessesAdapter).addBusinesses(businessesResponse.businesses) }
                )
                })
                .doOnError({ error -> Log.d("Error", error.message)})
                .subscribe()
    }
}
