package online.forgottenbit.nutrilicious.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.OnConflictStrategy.IGNORE
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