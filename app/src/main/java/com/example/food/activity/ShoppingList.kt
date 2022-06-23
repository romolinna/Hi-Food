package com.example.food.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food.R
import com.example.food.bd.DbHelper
import com.example.food.databinding.ShoppingListBinding
import com.example.food.databinding.TableBinding
import com.example.food.model.AdapterProduct
import com.example.food.model.ModelProduct

class ShoppingList :  AppCompatActivity(), AdapterProduct.Listener {
    private lateinit var binding: ShoppingListBinding
    private var db = DbHelper(this)
    val items: MutableList<ModelProduct> = mutableListOf()
    val productCheck: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = Html.fromHtml("<font color=\"black\" face=\"Poiret One\"><i>" + getString(R.string.app_name) + "</i></font>")
    }

    override fun onResume() {
        super.onResume()
        binding.navigationMenu3.setSelectedItemId(R.id.page_shoppingList)

        binding.navigationMenu3.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.page_main -> { startActivity(Intent(this, CategoryMain::class.java))
                    overridePendingTransition(0,0);}
                R.id.page_fav -> {startActivity(Intent(this, Favorites::class.java))
                    overridePendingTransition(0,0);}
                R.id.page_shoppingList -> {return@setOnNavigationItemSelectedListener true}
                R.id.page_table -> {startActivity(Intent(this, Table::class.java))
                    overridePendingTransition(0,0);}
                R.id.page_myrecipe -> {startActivity(Intent(this, MyRecipe::class.java))
                    overridePendingTransition(0,0);}
            }
            true
        }

        val arr: MutableList<String> = getProduct()

        val adapter = AdapterProduct(this)
        binding.shoppingList.adapter = adapter
        binding.shoppingList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        fillItems(arr);
        adapter.loadItems(items);

        binding.btnDelList.setOnClickListener {
            for (i in productCheck) {this.db.delShoppingList(i)}
            items.clear()
            fillItems(getProduct());
            adapter.loadItems(items);
        }
    }

    fun getProduct(): MutableList<String> {
        val a = this.db.getShoppingList()
        var list : MutableList<String> = mutableListOf()
        for(i in a){list.add(i.getValue("Good_name"))}
        return list
    }

    fun fillItems(arr: MutableList<String>){
        for(i in arr){
            val model = ModelProduct(i)
            items.add((model))
        }
    }

    override fun onClickListener(position: Int) {
        if (items[position].getName() in productCheck){
            productCheck.remove(items[position].getName())
        }else{
            productCheck.add(items[position].getName())
        }
    }
}