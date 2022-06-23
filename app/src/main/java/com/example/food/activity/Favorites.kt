package com.example.food.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.food.R
import com.example.food.bd.DbHelper
import com.example.food.databinding.MainRecipeBinding
import com.example.food.model.Adapter

class Favorites : AppCompatActivity(), Adapter.Listener {
    private lateinit var binding: MainRecipeBinding
    private val adapter = Adapter(this, this)
    private var db = DbHelper(this)
    private var pageNum = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = Html.fromHtml("<font color=\"black\" face=\"Poiret One\"><i>" + getString(R.string.app_name) + "</i></font>")
    }

    override fun onResume() {
        super.onResume()

        binding.navigationMenu1.setSelectedItemId(R.id.page_fav)
        init(this.db.getFavorites(pageNum))
        checkPage()

        binding.btnNext.setOnClickListener {
            pageNum += 1
            checkPage()
            init(this.db.getFavorites(pageNum))
        }

        binding.btnPrev.setOnClickListener {
            pageNum -= 1
            checkPage()
            init(this.db.getFavorites(pageNum))
        }

        binding.navigationMenu1.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.page_main -> {startActivity(Intent(this, CategoryMain::class.java))
                    overridePendingTransition(0,0);}
                R.id.page_fav -> {return@setOnNavigationItemSelectedListener true}
                R.id.page_shoppingList -> {startActivity(Intent(this, ShoppingList::class.java))
                    overridePendingTransition(0,0);}
                R.id.page_table -> {startActivity(Intent(this, Table::class.java))
                    overridePendingTransition(0,0);}
                R.id.page_myrecipe -> {startActivity(Intent(this, MyRecipe::class.java))
                    overridePendingTransition(0,0);}
            }
            true
        }
    }

    private fun init(data: MutableList<MutableMap<String, String>>) {
        var recycler: RecyclerView = findViewById(R.id.recyclerView_main)
        var arr: MutableList<MutableMap<String, String>> = mutableListOf()

        recycler.layoutManager = GridLayoutManager(this@Favorites, 2)
        recycler.adapter = adapter
        adapter.clearList()
        adapter.addRecipe(data)
    }

    fun checkPage() {
        val maxPage: Int
        val amount : Int = this.db.getPageFavRecipe()[0].getValue("amount").toInt()
        if (amount  <= 20){
            binding.btnPrev.visibility = View.GONE
            binding.btnNext.visibility = View.GONE
        }else{if (amount.rem(20) != 0){
            maxPage = (amount.div(20) + 1)
        }else{
            maxPage = amount.div(20) }
            if(pageNum == 1){
                binding.btnPrev.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE}
            if ((1 < pageNum) and (pageNum < maxPage)){
                binding.btnPrev.visibility = View.VISIBLE
                binding.btnNext.visibility = View.VISIBLE}
            if (pageNum == maxPage){
                binding.btnNext.visibility = View.GONE
                binding.btnPrev.visibility = View.VISIBLE
            }
        }
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
