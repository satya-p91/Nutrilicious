package online.forgottenbit.nutrilicious.data

import android.content.Context
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import online.forgottenbit.nutrilicious.data.db.AppDatabase
import online.forgottenbit.nutrilicious.data.db.DB
import online.forgottenbit.nutrilicious.data.db.dbScope
import online.forgottenbit.nutrilicious.data.network.NETWORK
import online.forgottenbit.nutrilicious.data.network.dto.DetailsDto
import online.forgottenbit.nutrilicious.data.network.dto.DetailsWrapper
import online.forgottenbit.nutrilicious.data.network.usdaApi
import online.forgottenbit.nutrilicious.model.FoodDetails
import retrofit2.Call

class DetailsRepository(ctx: Context) {

    private val detailsDao by lazy { AppDatabase.getInstance(ctx).detailsDao() }

    fun add(details: FoodDetails) = dbScope.launch { detailsDao.insert(details) }

    suspend fun getDetails(id: String): FoodDetails? {
        return withContext(DB) { detailsDao.loadById(id) }       // Prefers database
                ?: withContext(NETWORK) { fetchDetailsFromApi(id) }  // Falls back to network
                        .also { if (it != null) this.add(it) } // Adds newly fetched foods to DB
    }

    private suspend fun fetchDetailsFromApi(id: String): FoodDetails? {
        val request: Call<DetailsWrapper<DetailsDto>> = usdaApi.getDetails(id)

        val detailsDto: DetailsDto = withContext(NETWORK) {
            request.execute().body()?.foods?.get(0)?.food
        } ?: return null

        return FoodDetails(detailsDto)
    }
}