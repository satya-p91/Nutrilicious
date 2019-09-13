package online.forgottenbit.nutrilicious.view.main

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import online.forgottenbit.nutrilicious.MainActivity
import online.forgottenbit.nutrilicious.R
import online.forgottenbit.nutrilicious.data.network.networkScope
import online.forgottenbit.nutrilicious.model.Food
import online.forgottenbit.nutrilicious.view.common.bgScope
import online.forgottenbit.nutrilicious.view.common.getViewModel
import online.forgottenbit.nutrilicious.view.model.FavoritesViewModel
import online.forgottenbit.nutrilicious.view.model.SearchViewModel

class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel

    private var lastSearch = ""

    private lateinit var favoritesViewModel: FavoritesViewModel

    val UI = Dispatchers.Main + SupervisorJob()

    private var lastResults = emptyList<Food>()  // Import Food!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        searchViewModel = getViewModel(SearchViewModel::class)
        favoritesViewModel = getViewModel(FavoritesViewModel::class)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSearchRecyclerView()
        setUpSwipeRefresh()
        (rvFoods?.adapter as? SearchListAdapter)?.setItems(lastResults)

        if(lastResults.isEmpty()){
            updateListFor("raw")
        }else{
            updateListFor(lastSearch)
        }

    }

    private fun setUpSearchRecyclerView() {
        (activity as? MainActivity)?.setUpRecyclerView(rvFoods)
    }

    private fun setUpSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            updateListFor(lastSearch)
        }
    }

    fun updateListFor(searchTerm: String) {
        lastSearch = searchTerm
        swipeRefresh?.isRefreshing = true

        bgScope.launch {
            val favoritesIds: List<String> = favoritesViewModel.getAllIds()
            val foods: List<Food> = searchViewModel.getFoodsFor(searchTerm)
                .onEach { if (favoritesIds.contains(it.id)) it.isFavorite = true }
            lastResults = foods

            withContext(UI) {
                (rvFoods?.adapter as? SearchListAdapter)?.setItems(foods)
                swipeRefresh?.isRefreshing = false
            }
        }
    }
}