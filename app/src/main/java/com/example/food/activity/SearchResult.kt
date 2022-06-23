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
import java.io.Serializable

class SearchResult : AppCompatActivity(), Adapter.Listener {
    private lateinit var binding: MainRecipeBinding
    private val adapter = Adapter(this, this)
    private var db = DbHelper(this)
//    private var pageNum = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = Html.fromHtml("<font color=\"black\" face=\"Poiret One\"><i>" + getString(R.string.app_name) + "</i></font>")
//        adapter.setHasStableIds(true)
    }

    override fun onResume() {
        super.onResume()
        binding.btnPrev.visibility = View.GONE
        binding.btnNext.visibility = View.GONE
//        adapter.setHasStableIds(true)

        binding.navigationMenu1.setSelectedItemId(R.id.page_main)
        getId()

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
        recycler.layoutManager = GridLayoutManager(this@SearchResult,2)
        recycler.adapter =adapter
        adapter.clearList()
        adapter.addRecipe(data)
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

    fun getId() {
        if (intent.getSerializableExtra("idCook") == null) {
            init(this.db.searchRecipeName(intent.getSerializableExtra("name").toString()))
        }else{
            init(this.db.searchRecipe( intent.getSerializableExtra("idCook").toString()))
        }
    }

}