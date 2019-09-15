package online.forgottenbit.nutrilicious

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.rv_item.*
import kotlinx.android.synthetic.main.rv_item.tvFoodName
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import online.forgottenbit.nutrilicious.model.FoodDetails
import online.forgottenbit.nutrilicious.model.Nutrient
import online.forgottenbit.nutrilicious.model.NutrientType
import online.forgottenbit.nutrilicious.view.common.UI
import online.forgottenbit.nutrilicious.view.common.bgScope
import online.forgottenbit.nutrilicious.view.common.getViewModel
import online.forgottenbit.nutrilicious.view.model.DetailsViewModel

const val FOOD_ID_EXTRA = "NDBNO"

class DetailsActivity : AppCompatActivity() {

    private lateinit var detailsViewModel: DetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        detailsViewModel = getViewModel(DetailsViewModel::class)

        val foodId = intent.getStringExtra(FOOD_ID_EXTRA)  // Reads out desired food’s ID
        updateUiWith(foodId)
    }

    private fun updateUiWith(foodId: String) {
        if (foodId.isBlank()) return

        bgScope.launch {
            val details = detailsViewModel.getDetails(foodId)
            withContext(UI) { bindUi(details) }
        }
    }

    private fun bindUi(details: FoodDetails?) {
        if (details != null) {
            tvFoodName.text = "${details.name} (100g)"
            tvProximates.text = makeSection(details, NutrientType.PROXIMATES)
            tvMinerals.text = makeSection(details, NutrientType.MINERALS)
            tvVitamins.text = makeSection(details, NutrientType.VITAMINS)
            tvLipids.text = makeSection(details, NutrientType.LIPIDS)
        } else {
            tvFoodName.text = getString(R.string.no_data)
        }
    }

    private fun makeSection(details: FoodDetails, forType: NutrientType) =
            details.nutrients.filter { it.type == forType }
                    .joinToString(separator = "\n", transform = ::renderNutrient)

    private fun renderNutrient(nutrient: Nutrient): String = with(nutrient) {
        val displayName = name.substringBefore(",")  // = whole name if no comma
        "$displayName: $amountPer100g$unit"
    }

}
