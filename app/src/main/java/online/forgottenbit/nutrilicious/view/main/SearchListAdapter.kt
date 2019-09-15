package online.forgottenbit.nutrilicious.view.main

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.rv_item.*
import online.forgottenbit.nutrilicious.R
import online.forgottenbit.nutrilicious.model.Food

class SearchListAdapter(private var items: List<Food>,
                        private val onItemClick: (Food) -> Unit,
                        private val onStarClick: (Food, Int) -> Unit) :
        androidx.recyclerview.widget.RecyclerView.Adapter<SearchListAdapter.ViewHolder> (){


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

        val  view =  LayoutInflater.from(p0.context).inflate(R.layout.rv_item,p0,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int =items.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        p0.bindTo(items[p1])

    }

    // In this app, we'll usually replace all items so DiffUtil has little use
    fun setItems(newItems: List<Food>) {
        this.items = newItems   // Replaces whole list
        notifyDataSetChanged()  // Notifies recycler view of data changes to re-render
    }



    inner class ViewHolder(
        override val containerView: View
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindTo(food: Food) {  // Populates text views and star image to show a food
            tvFoodName.text = food.name
            tvFoodType.text = food.type

            val image = if (food.isFavorite) {
                android.R.drawable.btn_star_big_on
            } else {
                android.R.drawable.btn_star_big_off
            }
            ivStar.setImageResource(image)

            ivStar.setOnClickListener { onStarClick(food, this.layoutPosition) }
            containerView.setOnClickListener { onItemClick(food) }
        }
    }

}