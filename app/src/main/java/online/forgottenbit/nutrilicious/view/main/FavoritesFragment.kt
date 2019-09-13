package online.forgottenbit.nutrilicious.view.main

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_favorites.*
import kotlinx.coroutines.launch
import online.forgottenbit.nutrilicious.MainActivity
import online.forgottenbit.nutrilicious.R
import online.forgottenbit.nutrilicious.model.Food
import online.forgottenbit.nutrilicious.view.common.UI
import online.forgottenbit.nutrilicious.view.common.bgScope
import online.forgottenbit.nutrilicious.view.common.getViewModel
import online.forgottenbit.nutrilicious.view.model.FavoritesViewModel
import java.util.*

class FavoritesFragment :Fragment(){


    private lateinit var favoritesViewModel: FavoritesViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        favoritesViewModel = getViewModel(FavoritesViewModel::class)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        observeFavorites()
    }

    private fun setUpRecyclerView() {
        (activity as? MainActivity)?.setUpRecyclerView(rvFavorites, emptyList())
    }

    private fun observeFavorites() = bgScope.launch {
        val favorites = favoritesViewModel.getFavorites()
        favorites.observe(this@FavoritesFragment, Observer { foods ->
            foods?.let {
                launch(UI) { (rvFavorites.adapter as? SearchListAdapter)?.setItems(foods) }
            }
        })
    }

}