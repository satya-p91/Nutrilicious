package online.forgottenbit.nutrilicious.data.db

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import online.forgottenbit.nutrilicious.model.Nutrient
import online.forgottenbit.nutrilicious.model.NutrientType

class NutrientListConverter {
    private val moshi = Moshi.Builder().build()
    private val nutrientList = Types.newParameterizedType( // Represents List<Nutrient>
            List::class.java, Nutrient::class.java
    )
    private val adapter = moshi.adapter<List<Nutrient>>(nutrientList) // Builds adapter

    @TypeConverter
    fun toString(nutrient: List<Nutrient>): String = adapter.toJson(nutrient)

    @TypeConverter fun toListOfNutrient(json: String): List<Nutrient>
            = adapter.fromJson(json) ?: emptyList()
}

class NutrientTypeConverter {

    @TypeConverter
    fun toString(nutrientType: NutrientType) = nutrientType.name   // Type -> String

    @TypeConverter
    fun toNutrientType(name: String) = NutrientType.valueOf(name)  // String -> Type
}