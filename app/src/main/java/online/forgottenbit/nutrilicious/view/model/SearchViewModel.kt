package online.forgottenbit.nutrilicious.view.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.withContext
import online.forgottenbit.nutrilicious.data.network.NETWORK
import online.forgottenbit.nutrilicious.data.network.dto.FoodDto
import online.forgottenbit.nutrilicious.data.network.dto.SearchWrapper
import online.forgottenbit.nutrilicious.data.network.usdaApi
import online.forgottenbit.nutrilicious.model.Food
import retrofit2.Call

class SearchViewModel : ViewModel(){

    suspend fun getFoodsFor(searchTerm: String): List<Food> {
        val request: Call<SearchWrapper<List<FoodDto>>> = usdaApi.getFoods(searchTerm)
        val foodDtos: List<FoodDto> = withContext(NETWORK) { doRequest(request) }
        return foodDtos.map(::Food)
    }

    private fun doRequest(req: Call<SearchWrapper<List<FoodDto>>>):List<FoodDto> = req.execute().body()?.list?.item ?: emptyList()
}