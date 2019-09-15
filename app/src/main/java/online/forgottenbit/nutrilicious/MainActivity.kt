package online.forgottenbit.nutrilicious

import android.Manifest
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.supportgenie.androidlibrary.LibraryActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import online.forgottenbit.nutrilicious.view.common.getViewModel
import online.forgottenbit.nutrilicious.model.Food
import online.forgottenbit.nutrilicious.view.common.addFragmentToState
import online.forgottenbit.nutrilicious.view.common.replaceFragment
import online.forgottenbit.nutrilicious.view.main.FavoritesFragment
import online.forgottenbit.nutrilicious.view.main.SearchFragment
import online.forgottenbit.nutrilicious.view.main.SearchListAdapter
import online.forgottenbit.nutrilicious.view.model.FavoritesViewModel

private const val SEARCH_FRAGMENT_TAG = "SEARCH_FRAGMENT"

class MainActivity : AppCompatActivity() {


    internal var permission_req_code = 1000


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


    fun setUpRecyclerView(rv: androidx.recyclerview.widget.RecyclerView, list: List<Food> = emptyList()) {
        with(rv) {
            adapter = setUpSearchListAdapter(rv, list)
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            addItemDecoration(
                androidx.recyclerview.widget.DividerItemDecoration(
                    context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
                )
            )
            setHasFixedSize(true)
        }
    }

    private fun setUpSearchListAdapter(rv: androidx.recyclerview.widget.RecyclerView, items: List<Food>) = SearchListAdapter(
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if(item?.itemId == R.id.support){
            if(!defaultPermissionCheck()){
                askForPermission()
            }else{
                startActivity(Intent(this,LibraryActivity::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEARCH) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            searchFragment.updateListFor(query)
        }
    }

    private fun askForPermission() {

        //asking  for storage permission from user at runtime

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE
            ),
            permission_req_code
        )


    }


    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {

        //checking if user granted the permissions or not

        if (requestCode == permission_req_code) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted :)", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this,
                    "App will not work without permissions, Grant these permissions from settings. :|",
                    Toast.LENGTH_LONG
                ).show()
                askForPermission()
            }
        }
    }

    private fun defaultPermissionCheck(): Boolean {
        //checking if permissions is already granted
        val internet = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.INTERNET)
        val access = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_WIFI_STATE)
        val change = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_NETWORK_STATE)
        val bt = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WAKE_LOCK)
        val btAdmin = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)

        val external_storage_write = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return internet == PackageManager.PERMISSION_GRANTED  &&
                access == PackageManager.PERMISSION_GRANTED && change == PackageManager.PERMISSION_GRANTED && bt == PackageManager.PERMISSION_GRANTED &&
                btAdmin == PackageManager.PERMISSION_GRANTED && external_storage_write == PackageManager.PERMISSION_GRANTED
    }
}
