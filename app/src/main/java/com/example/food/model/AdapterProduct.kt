package com.example.food.model

import com.example.food.R
import android.content.Context
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.food.databinding.ProductItemRecipeBinding


class AdapterProduct (val listener: Listener) : RecyclerView.Adapter<AdapterProduct.ViewHolder>() {
    private var items: List<ModelProduct>? = ArrayList()
    var itemStateArray = SparseBooleanArray()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val layoutForItem: Int = R.layout.product_item_recipe
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(layoutForItem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, listener)
    }

    override fun getItemCount(): Int {
        return if (items == null) {
            0
        } else items!!.size
    }

    fun loadItems(tournaments: List<ModelProduct>?) {
        items = tournaments
        notifyDataSetChanged()
    }
    interface Listener{
        fun onClickListener(position: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val binding = ProductItemRecipeBinding.bind(itemView)
        fun bind(position: Int, listener: Listener) {
            if (!itemStateArray[position, false]) {
                binding.checkProductItem.isChecked = false
            } else {
                binding.checkProductItem.isChecked = true
            }
            binding.checkProductItem.text = items!![position].getName()
        }

        override fun onClick(v: View?) {
            val adapterPosition = adapterPosition
            if (!itemStateArray[adapterPosition, false]) {
                binding.checkProductItem.isChecked = true
                itemStateArray.put(adapterPosition, true)
                Log.d("ddd", items!![adapterPosition].toString())
            } else {
                Log.d("ddd", items!![adapterPosition].toString())
                binding.checkProductItem.isChecked = false
                itemStateArray.put(adapterPosition, false)
            }
        }

        init {
            itemView.setOnClickListener {
                listener.onClickListener(position)
            }
        }
    }
}