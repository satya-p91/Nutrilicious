package online.forgottenbit.nutrilicious.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import online.forgottenbit.nutrilicious.data.network.dto.FoodDto

@Entity(tableName = "favorites")
data class Food (
        @PrimaryKey val id: String,
        val name: String,
        val type: String,
        var isFavorite : Boolean = false){

    constructor(dto: FoodDto) : this(dto.ndbno,dto.name,dto.group)
}