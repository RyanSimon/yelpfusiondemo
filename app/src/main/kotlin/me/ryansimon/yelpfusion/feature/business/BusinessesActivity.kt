package me.ryansimon.yelpfusion.feature.business

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.CursorAdapter
import android.support.v4.widget.SimpleCursorAdapter
import android.support.v7.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import me.ryansimon.yelpfusion.extension.hideKeyboard
import android.provider.BaseColumns
import android.database.MatrixCursor
import me.ryansimon.yelpfusion.R


/**
 * @author Ryan Simon
 */
class BusinessesActivity : AppCompatActivity() {

    private val suggestions = mutableListOf<String>()
    private val suggestionsId = "suggestions"
    private lateinit var searchView: SearchView
    private lateinit var businessesViewModel: BusinessesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        businessesViewModel = ViewModelProviders.of(this, BusinessesViewModelFactory(application))
                .get(BusinessesViewModel::class.java)
        businessesViewModel.businessesObservable.observe(this, Observer<List<Business>> {
            it?.let {
                (business_list_view.adapter as BusinessesAdapter).updateBusinesses(it)
            }
        })

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

                businessesViewModel.userSubmittedPaginatedSearch(query)

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
                    businessesViewModel.userSubmittedPaginatedSearch(it)
                }

                return true
            }

            override fun onSuggestionSelect(position: Int): Boolean {
                val suggestion = searchView.suggestionsAdapter.getItem(position) as? String
                suggestion?.let {
                    businessesViewModel.userSubmittedPaginatedSearch(it)
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
                businessesViewModel.userSubmittedPaginatedSearch(searchView.query.toString(), false)
            }
        }
        businessListView.setHasFixedSize(true)
        businessListView.addOnScrollListener(scrollListener)
        businessListView.adapter = BusinessesAdapter()
    }
}
