package me.ryansimon.yelpfusion.feature.business.presentation

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import androidx.recyclerview.widget.RecyclerView
import me.ryansimon.yelpfusion.core.extension.hideKeyboard
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.ryansimon.yelpfusion.R
import me.ryansimon.yelpfusion.core.extension.queryTextChangeEvents
import me.ryansimon.yelpfusion.core.EndlessRecyclerViewScrollListener
import me.ryansimon.yelpfusion.feature.business.domain.BusinessAndTopReview


/**
 * @author Ryan Simon
 */
class BusinessesActivity : AppCompatActivity() {

    private lateinit var businessesViewModel: BusinessesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        businessesViewModel = ViewModelProviders.of(this, BusinessesViewModelFactory(application))
                .get(BusinessesViewModel::class.java)
        businessesViewModel.businessesObservable.observe(this, Observer<List<BusinessAndTopReview>> {
            it?.let {
                (businessListView.adapter as BusinessesAdapter).updateBusinesses(it)
            }
        })

        setupBusinessList()
        setupSearchView()
    }

    private fun setupSearchView() {
        searchView.setIconifiedByDefault(false)
        searchView.queryTextChangeEvents()
                .onEach {
                    val query = searchView.query.toString()

                    if (it.isSubmitted) {
                        searchSubmitted(query)
                    }
                }
                .launchIn(lifecycleScope)
    }

    private fun searchSubmitted(query: String) {
        val adapter = businessListView.adapter as BusinessesAdapter
        adapter.clear()

        businessesViewModel.userSubmittedPaginatedSearch(query)

        hideKeyboard()
        hideSearchEditCursor()
    }

    private fun setupBusinessList() {
        val businessListView = businessListView
        val scrollListener = object : EndlessRecyclerViewScrollListener(businessListView.layoutManager as GridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                businessesViewModel.userSubmittedPaginatedSearch(searchView.query.toString(), false)
            }
        }
        businessListView.setHasFixedSize(true)
        businessListView.addOnScrollListener(scrollListener)
        businessListView.adapter = BusinessesAdapter()
    }

    private fun hideSearchEditCursor() {
        businessListView.requestFocus()
    }
}
