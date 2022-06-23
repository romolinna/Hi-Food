package com.example.food.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.food.R
import com.example.food.bd.DbHelper
import com.example.food.databinding.MainRecipeBinding
import com.example.food.model.Adapter

class RecipeMain  : AppCompatActivity(), Adapter.Listener {
    private lateinit var binding: MainRecipeBinding
    private val adapter = Adapter(this, this)
    private var db = DbHelper(this)
    private var pageNum = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        adapter.setHasStableIds(true)
        supportActionBar?.title = Html.fromHtml("<font color=\"black\" face=\"Poiret One\"><i>" + getString(R.string.app_name) + "</i></font>")
    }

    override fun onResume() {
        super.onResume()
        binding.navigationMenu1.setSelectedItemId(R.id.page_main)
        init(this.db.getRecipes(pageNum, getSubCatId()))
        checkPage()

        binding.btnNext.setOnClickListener {
            pageNum += 1
            checkPage()
            init(this.db.getRecipes(pageNum, getSubCatId()))
        }

        binding.btnPrev.setOnClickListener {
            pageNum -= 1
            checkPage()
            init(this.db.getRecipes(pageNum, getSubCatId()))
        }

        binding.navigationMenu1.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.page_main -> { return@setOnNavigationItemSelectedListener true}
                R.id.page_fav -> {startActivity(Intent(this, Favorites::class.java))
                    overridePendingTransition(0,0);}
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.search ->{ startActivity(Intent(this, Search::class.java))}
        }
            return true
    }

    private  fun init(data : MutableList<MutableMap<String,String>>){
        var recycler: RecyclerView = findViewById(R.id.recyclerView_main)
        recycler.layoutManager = GridLayoutManager(this@RecipeMain,2)
        recycler.adapter =adapter
        adapter.clearList()
        adapter.addRecipe(data)
    }

    fun checkPage() {
        val maxPage: Int
        val amountRecipe: Int = this.db.getPageRecipe(getSubCatId())[0].getValue("amount").toInt()
        if (amountRecipe <= 20) {
            binding.btnPrev.visibility = View.GONE
            binding.btnNext.visibility = View.GONE
        } else {
            if ((amountRecipe.rem(20)) != 0) {
                maxPage = amountRecipe.div(20) + 1
            } else {
                maxPage = amountRecipe.div(20) }
            if (pageNum == 1) {
                binding.btnPrev.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE }
            if ((1 < pageNum) and (pageNum < maxPage)) {
                binding.btnPrev.visibility = View.VISIBLE
                binding.btnNext.visibility = View.VISIBLE }
            if (pageNum == maxPage) {
                binding.btnNext.visibility = View.GONE
                binding.btnPrev.visibility = View.VISIBLE }
        }
    }

    override fun onClick(recipes: MutableMap<String,String>) {
        val intent = Intent(this, Recipe::class.java)
        intent.putExtra("is_favorite", recipes.getValue("is_favorite"))
        intent.putExtra("is_official", recipes.getValue("is_official"))
        intent.putExtra("image", recipes.getValue("image"))
        intent.putExtra("name", recipes.getValue("name"))
        intent.putExtra("Cooking_id", recipes.getValue("Cooking_id"))
        intent.putExtra("id", recipes.getValue("id"))
        startActivity(intent)
    }
    fun getSubCatId(): Int {
        return intent.getSerializableExtra("id").toString().toInt()
    }
}