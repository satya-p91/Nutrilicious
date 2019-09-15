package online.forgottenbit.nutrilicious.view.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import online.forgottenbit.nutrilicious.data.DetailsRepository
import online.forgottenbit.nutrilicious.model.FoodDetails

class DetailsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = DetailsRepository(app)
    suspend fun getDetails(foodId: String): FoodDetails? = repo.getDetails(foodId)
}