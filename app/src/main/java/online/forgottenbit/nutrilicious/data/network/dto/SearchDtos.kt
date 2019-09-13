package online.forgottenbit.nutrilicious.data.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ListWrapper<T>{
    var list: T? = null
}

class ItemWrapper<T>{
    var item:T? = null
}
typealias SearchWrapper<T> = ListWrapper<ItemWrapper<T>>

@JsonClass(generateAdapter = true)
class FoodDto{
    lateinit var ndbno: String
    lateinit var name: String
    lateinit var group: String
}