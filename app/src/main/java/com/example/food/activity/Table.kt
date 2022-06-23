package com.example.food.activity

import android.app.ActionBar
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.food.R
import com.example.food.bd.DbHelper
import com.example.food.databinding.TableBinding


class Table :  AppCompatActivity() {
    private lateinit var binding: TableBinding
    private var db = DbHelper(this)
    var product : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TableBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = Html.fromHtml("<font color=\"black\" face=\"Poiret One\"><i>" + getString(R.string.app_name) + "</i></font>")

        binding.navigationMenu4.setSelectedItemId(R.id.page_table)

        binding.navigationMenu4.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.page_main -> {
                    startActivity(Intent(this, CategoryMain::class.java))
                    overridePendingTransition(0, 0);
                }
                R.id.page_fav -> {
                    startActivity(Intent(this, Favorites::class.java))
                    overridePendingTransition(0, 0);
                }
                R.id.page_shoppingList -> {
                    startActivity(Intent(this, ShoppingList::class.java))
                    overridePendingTransition(0, 0);
                }
                R.id.page_table -> {
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.page_myrecipe -> {
                    startActivity(Intent(this, MyRecipe::class.java))
                    overridePendingTransition(0, 0);
                }
            }
            true
        }

        val adapter = ArrayAdapter(applicationContext, R.layout.table_item, getProduct(this.db.getTable()))
        binding.product.adapter = adapter

        binding.product.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                product = binding.product.selectedItem.toString()
                setAmount(product)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    fun getProduct(arr : MutableList<MutableMap<String, String>>): MutableList<String> {
        var m : MutableList<String> = mutableListOf()
        for(i in arr){
            m.add(i.getValue("product"))
        }
        return m
    }

    fun setAmount(name:String){
        var arr = this.db.getAmountProduct(name)
        for(i in arr){
            binding.cupAmount.text = i.getValue("in_cup")
            binding.spoonAmount.text = i.getValue("in_tablespoon")
            binding.teaspoonAmount.text = i.getValue("in_teaspoon")}
    }
}