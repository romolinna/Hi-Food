package com.example.food.model

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.food.R
import com.example.food.databinding.RecipeMainItemBinding


class Adapter(val listener: Listener, var context: Context) : RecyclerView.Adapter<Adapter.MyViewHolder>() {

    private var recipesList : MutableList<MutableMap<String, String>> = mutableListOf()

    class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = RecipeMainItemBinding.bind(item)

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(arr: MutableMap<String, String>, listener: Listener, context: Context){
            if (arr.getValue("image").isEmpty()){
                binding.recipeImageItem.setImageResource(R.drawable.image)
            }else{
                binding.recipeImageItem.setImageDrawable(Drawable.createFromPath("/data/data/com.example.food/images/" + arr.getValue("image")))
            }
            binding.recipeNameItem.text = arr.getValue("name")
            itemView.setOnClickListener {
                listener.onClick(arr)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_main_item, parent, false)
        return MyViewHolder(view)
    }

    fun clearList(){
        recipesList.clear()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        return holder.bind(recipesList[position], listener, context)
    }

    override fun getItemCount(): Int {
        return  recipesList.size
    }

    fun addRecipe(recipes: MutableMap<String, String>){
        recipesList.add(recipes)
        notifyDataSetChanged()
    }
    fun addRecipe(recipes: MutableList<MutableMap<String, String>>){
        recipesList = recipes
        notifyDataSetChanged()
    }

    interface Listener{
        fun onClick(recipes: MutableMap<String, String>)
    }
}