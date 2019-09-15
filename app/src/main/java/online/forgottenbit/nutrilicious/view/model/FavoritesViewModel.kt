package online.forgottenbit.nutrilicious.view.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import online.forgottenbit.nutrilicious.data.db.AppDatabase
import online.forgottenbit.nutrilicious.data.db.DB
import online.forgottenbit.nutrilicious.data.db.dbScope
import online.forgottenbit.nutrilicious.model.Food
import java.util.concurrent.Executors

class FavoritesViewModel (app : Application) : AndroidViewModel(app){


    private val dao by lazy { AppDatabase.getInstance(getApplication()).favoritesDao()}

    suspend fun getFavorites(): LiveData<List<Food>> = withContext(DB) {
        dao.loadAll()
    }

    suspend fun getAllIds(): List<String> = withContext(DB) { dao.loadAllIds() }

    fun add(favorite: Food) = dbScope.launch { dao.insert(favorite) }

    fun delete(favorite: Food) = dbScope.launch { dao.delete(favorite) }

}