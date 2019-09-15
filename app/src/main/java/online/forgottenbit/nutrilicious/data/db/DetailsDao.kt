package online.forgottenbit.nutrilicious.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import online.forgottenbit.nutrilicious.model.FoodDetails

@Dao
interface DetailsDao {

    @Query("SELECT * FROM details WHERE id = :ndbno")
    fun loadById(ndbno: String): FoodDetails?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(food: FoodDetails)
}