package online.forgottenbit.nutrilicious

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.SearchView
import android.view.Menu
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import online.forgottenbit.nutrilicious.view.common.getViewModel
import online.forgottenbit.nutrilicious.data.network.networkScope
import online.forgottenbit.nutrilicious.data.network.usdaApi
import online.forgottenbit.nutrilicious.model.Food
import online.forgottenbit.nutrilicious.view.common.addFragmentToState
import online.forgottenbit.nutrilicious.view.common.replaceFragment
import online.forgottenbit.nutrilicious.view.main.FavoritesFragment
import online.forgottenbit.nutrilicious.view.main.SearchFragment
import online.forgottenbit.nutrilicious.view.main.SearchListAdapter
import online.forgottenbit.nutrilicious.view.model.FavoritesViewModel
import online.forgottenbit.nutrilicious.view.model.SearchViewModel

private const val SEARCH_FRAGMENT_TAG = "SEARCH_FRAGMENT"

class MainActivity : AppCompatActivity() {


    val UI = Dispatchers.Main + SupervisorJob()

    private lateinit var searchFragment: SearchFragment

    private lateinit var favoritesViewModel: FavoritesViewModel


    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                replaceFragment(R.id.mainView, searchFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_my_food -> {
                replaceFragment(R.id.mainView, FavoritesFragment())
                return@OnNavigationItemSelectedListener true
            }

        }
        false
    }


    fun setUpRecyclerView(rv: RecyclerView, list: List<Food> = emptyList()) {
        with(rv) {
            adapter = setUpSearchListAdapter(rv, list)
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                    DividerItemDecoration(
                            context, LinearLayoutManager.VERTICAL
                    )
            )
            setHasFixedSize(true)
        }
    }

    private fun setUpSearchListAdapter(rv: RecyclerView, items: List<Food>) = SearchListAdapter(
            items,
            onItemClick = { startDetailsActivity(it) },
            onStarClick = { food, layoutPosition ->
                toggleFavorite(food)
                rv.adapter?.notifyItemChanged(layoutPosition)
            })

    private fun startDetailsActivity(food: Food) {
        val intent = Intent(this, DetailsActivity::class.java).apply {
            putExtra(FOOD_ID_EXTRA, food.id)  // Stores the desired food’s ID in the Intent
        }
        startActivity(intent)  // Switches to DetailsActivity
    }


    private fun toggleFavorite(food: Food) {
        val wasFavoriteBefore = food.isFavorite
        food.isFavorite = food.isFavorite.not()  // Adjusts Food object’s favorite status

        if (wasFavoriteBefore) {
            favoritesViewModel.delete(food)
            Toast.makeText(this@MainActivity, "Removed ${food.name} from your favorites.", Toast.LENGTH_SHORT).show()
        } else {
            favoritesViewModel.add(food)
            Toast.makeText(this@MainActivity, "Added ${food.name} as a new favorite of yours!", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.navigation)

        recoverOrBuildSearchFragment()
        replaceFragment(R.id.mainView, searchFragment)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        favoritesViewModel = getViewModel(FavoritesViewModel::class)

    }

    private fun recoverOrBuildSearchFragment() {
        val fragment = supportFragmentManager
                .findFragmentByTag(SEARCH_FRAGMENT_TAG) as? SearchFragment
        if (fragment == null) {
            setUpSearchFragment()
        } else {
            searchFragment = fragment
        }
    }

    private fun setUpSearchFragment() {
        searchFragment = SearchFragment()
        addFragmentToState(R.id.mainView, searchFragment, SEARCH_FRAGMENT_TAG)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }

        return true
    }

    override fun onNewIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEARCH) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            searchFragment.updateListFor(query)
        }
    }
}
