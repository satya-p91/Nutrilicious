package online.forgottenbit.nutrilicious.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy.IGNORE
import online.forgottenbit.nutrilicious.model.Food

@Dao
interface FavoritesDao{

    @Query("Select * from favorites")
    fun loadAll() : LiveData<List<Food>>

    @Query("SELECT id FROM favorites")
    fun loadAllIds(): List<String>

    @Insert(onConflict = IGNORE)
    fun insert(food : Food)

    @Delete
    fun delete(food: Food)

}