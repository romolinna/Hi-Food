package com.example.food.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.food.R
import com.example.food.bd.DbHelper
import com.example.food.databinding.MainRecipeBinding
import com.example.food.databinding.SearchBinding
import com.example.food.model.Adapter

class Search : AppCompatActivity() {
    private lateinit var binding: SearchBinding
    private var db = DbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = Html.fromHtml("<font color=\"black\" face=\"Poiret One\"><i>" + getString(R.string.app_name) + "</i></font>")
    }

    override fun onResume() {
        super.onResume()

        val arr : MutableList<String> = mutableListOf()
        val adapter = ArrayAdapter(this, R.layout.item_for_search, arr)
        binding.searchList.adapter = adapter

        binding.searchAdd.setOnClickListener {
            if(binding.searchEdit.text.isNullOrEmpty()){}
            else{
                arr.add(binding.searchEdit.text.toString())
                binding.searchEdit.setText("")
                adapter.notifyDataSetChanged()
            }
        }

        binding.searchBtn.setOnClickListener {
            if (arr.size == 0){}
            else{
                val idCook = this.db.searchByGoods(arr).toString()

                val intent = Intent(this, SearchResult::class.java)
                intent.putExtra("idCook", idCook)
                startActivity(intent)
            }
        }

        binding.searchAdd2.setOnClickListener {
            if(binding.searchEdit2.text.isNullOrEmpty()){}
            else{
                val name = binding.searchEdit2.text.toString()

                val intent = Intent(this, SearchResult::class.java)
                intent.putExtra("name", name)
                startActivity(intent)
            }
        }
    }

//    fun idInString(arr:MutableList<String>): String {
//       val a =  this.db.findIdForSearch(arr)
//        var idList : String = ""
//        for (i in a){
//            for (j in i){
//            idList +=  j.getValue("id")
//            if (j != i[i.size-1]){ idList += ", "}}
//        }
////        Log.d("ddd", idList)
//        return idList
//    }

//    fun idInList(arr: List<String>): String {
//        var a : MutableList<String> = mutableListOf()
//        var s : String = ""
//        for (i in arr){a.add(i.getValue("Cooking_id"))}
//        for (i in a){
//            s +=  i
//            if (i != a[a.size-1]){s += ", "}}
//        return s
//    }
}
