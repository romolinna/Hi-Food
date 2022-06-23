package com.example.food.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.food.R
import com.example.food.bd.DbHelper
import com.example.food.databinding.MyRecipeBinding
import com.example.food.model.Adapter

class MyRecipe: AppCompatActivity(), Adapter.Listener {
    private lateinit var binding: MyRecipeBinding
    private val adapter = Adapter(this, this)
    private var db = DbHelper(this)
//    private var pageNum = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = Html.fromHtml("<font color=\"black\" face=\"Poiret One\"><i>" + getString(R.string.app_name) + "</i></font>")
//        adapter.setHasStableIds(true)

        binding.navigationMenu2.setSelectedItemId(R.id.page_myrecipe)
        init(this.db.getMyRecipe())

        binding.btnAddMyRecipe.setOnClickListener {
            startActivity(Intent(this, AddRecipe::class.java))
        }

        binding.navigationMenu2.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.page_main -> {startActivity(Intent(this, CategoryMain::class.java))
                    overridePendingTransition(0,0);}
                R.id.page_fav -> {startActivity(Intent(this, Favorites::class.java))
                    overridePendingTransition(0,0);}
                R.id.page_shoppingList -> {startActivity(Intent(this, ShoppingList::class.java))
                    overridePendingTransition(0,0);}
                R.id.page_table -> {startActivity(Intent(this, Table::class.java))
                    overridePendingTransition(0,0);}
                R.id.page_myrecipe -> {return@setOnNavigationItemSelectedListener true}
            }
            true
        }
    }


    private fun init(data: MutableList<MutableMap<String, String>>) {
        var recycler: RecyclerView = findViewById(R.id.recyclerView_myrecipe)

        recycler.layoutManager = GridLayoutManager(this@MyRecipe, 2)
        recycler.adapter = adapter
        adapter.clearList()
        adapter.addRecipe(data)
    }

    override fun onClick(recipes: MutableMap<String, String>) {
        val intent = Intent(this, Recipe::class.java)
        intent.putExtra("is_favorite", recipes.getValue("is_favorite"))
        intent.putExtra("image", recipes.getValue("image"))
        intent.putExtra("name", recipes.getValue("name"))
        intent.putExtra("Cooking_id", recipes.getValue("Cooking_id"))
        intent.putExtra("id", recipes.getValue("id"))
        startActivity(intent)
    }
}