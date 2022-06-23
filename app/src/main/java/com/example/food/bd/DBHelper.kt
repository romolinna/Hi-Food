package com.example.food.bd

import android.app.Activity
import android.content.res.AssetManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import android.util.Log
import java.io.*


class DbHelper(val context: Activity) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DB_PATH = "/data/data/com.example.food/databases/"
        const val DATABASE_NAME = "sqlite3.db"
        const val DATABASE_VERSION = 1
        const val PAGE_SIZE = 20
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        createDatabase()
        return super.getWritableDatabase()
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        createDatabase()
        return super.getReadableDatabase()
    }

    private fun installDatabaseFromAssets() {
        val inputStream = context.assets.open("database/sqlite3.db")

        try {
            val outputFile = File(DB_PATH + DATABASE_NAME)
            val outputStream = FileOutputStream(outputFile)

            inputStream.copyTo(outputStream)
            inputStream.close()

            outputStream.flush()
            outputStream.close()
        } catch (exception: Throwable) {
            throw RuntimeException("The $DATABASE_NAME database couldn't be installed.", exception)
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    private fun createDatabase(){
        val dbIsExist: Boolean = checkDataBase()
        if(!dbIsExist){
            installDatabaseFromAssets()
        }
    }

    private fun checkDataBase(): Boolean {
        val mPath: String = DB_PATH + DATABASE_NAME
        val file = File(mPath)
        return file.exists()
    }

    private fun queryToArrayMap(q: Cursor): MutableList<MutableMap<String, String>>{
        val columnCount = q.columnCount - 1
        val res = mutableListOf<MutableMap<String, String>>()
        while(q.moveToNext()) {
            val rowRes = mutableMapOf<String, String>()
            for (ind in 0..columnCount) {
                rowRes[q.getColumnName(ind).toString()] =  if(q.isNull(ind)) "" else q.getString(ind)
            }
            res.add(rowRes)
        }
        return res
    }

    private fun queryToArray(q: Cursor): MutableList<MutableList<String>>{
        Log.d("lll", "huy")
        val columnCount = q.columnCount - 1
        val res = mutableListOf<MutableList<String>>()
        Log.d("lll", q.columnCount.toString())
        while(q.moveToNext()) {
            val rowRes = mutableListOf<String>()
            for (ind in 0..columnCount) {
                rowRes.add(if (q.isNull(ind)) "" else q.getString(ind))
            }
            res.add(rowRes)
        }
        return res
    }

    fun getPageCategory(): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select count(*) as `amount` from Category ", null))
    }
    fun getPageSubCategory(idCategory: Int): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select count(*) as `amount` from Subcategory where category_id = $idCategory ", null))
    }
    fun getPageRecipe(idSubCategory: Int): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select count(*) as `amount` from Recipe where (SubCategory_id = $idSubCategory) and (is_official = 1) ", null))
    }

    fun getPageFavRecipe(): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select count(*) as `amount` from Recipe where is_favorite = 1", null))
    }

    fun getCategories(page: Int): MutableList<MutableMap<String, String>>{
        val db = this.readableDatabase
        val lim: Int = PAGE_SIZE * (page-1)
        return queryToArrayMap(db.rawQuery("select * from Category order by id ASC limit $lim, $PAGE_SIZE", null))
    }
    fun getSubCategories(page: Int, idCategory: Int): MutableList<MutableMap<String, String>>{
        val db = this.readableDatabase
        val lim: Int = PAGE_SIZE * (page-1)
        return queryToArrayMap(db.rawQuery("select * from Subcategory where category_id = $idCategory order by id ASC limit $lim, $PAGE_SIZE", null))
    }
    fun getRecipes(page: Int, idSubCategory: Int): MutableList<MutableMap<String, String>>{
        val db = this.readableDatabase
        val lim: Int = PAGE_SIZE * (page-1)
        return queryToArrayMap(db.rawQuery("select * from Recipe where (SubCategory_id = $idSubCategory) and (is_official = 1) order by id ASC limit $lim, $PAGE_SIZE", null))
    }

    fun getCooking(id: Int): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select cooking_text, note from Cooking where id = $id", null))
    }
    fun getGoods(cooking_id: Int): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select Good.name from Cooking_good inner join Good on Good.id = Cooking_Good.Good_id where Cooking_Good.Cooking_id = $cooking_id ", null))
    }

    fun getFavorites(page: Int): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        val lim: Int = PAGE_SIZE * (page-1)
        return queryToArrayMap(db.rawQuery("select * from Recipe where is_favorite = 1 order by id ASC limit $lim, $PAGE_SIZE", null))
    }

    fun getMyRecipe(): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select * from Recipe where is_official = 0 order by id ASC ", null))
    }

    fun getOfficial(id: Int): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select is_official from Recipe where id = $id  ", null))
    }

    fun getTable(): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select * from Measure", null))
    }

    fun setIsFav(recipe_id: Int){
        val db = this.writableDatabase
        val a = db.rawQuery("update Recipe set is_favorite = 1 where id = $recipe_id", null)
        a.moveToFirst();
        a.close();
    }

    fun setIsNotFav(recipe_id: Int){
        val db = this.writableDatabase
        val a = db.rawQuery("update Recipe set is_favorite = 0 where id = $recipe_id", null)
        a.moveToFirst();
        a.close();
    }

    fun setNote(cooking_id: Int, text: String){
        val db = this.writableDatabase
        val a = db.rawQuery("update Cooking set note = \"$text\" where id = $cooking_id", null)
        a.moveToFirst();
        a.close();
    }
    fun getNote(cooking_id: Int): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select note from Cooking where id = $cooking_id", null))
    }

//    fun isFavorite(recipe_id : Int): MutableList<MutableMap<String, String>> {
//        val db = this.readableDatabase
//        return queryToArray(db.rawQuery("select is_favorite from Recipe where id = $recipe_id", null))
//    }

    fun addCooking(text: String){
        val db = this.writableDatabase
        val a = db.rawQuery("insert into Cooking (cooking_text) values (\"$text\")", null)
        a.moveToFirst();
        a.close();
    }

    fun findCooking(): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select id from Cooking order by id DESC limit 1", null))
    }

    fun addRecipe(subCat_id: Int, cooking_id: Int, image: String, name: String){
        Log.d("rrr", subCat_id.toString() + " " + cooking_id.toString() + " " + image + " " + name)
        val db = this.writableDatabase
        val a = db.rawQuery("insert into Recipe (SubCategory_id, is_official, image, name, Cooking_id) values ($subCat_id, 0, \"$image\", \"$name\", $cooking_id)", null)
        a.moveToFirst();
        a.close();
    }

    fun getCatId(name: String): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select id from Category where name = \"$name\" ", null))
    }

    fun getAllSubCat(cat_id: Int): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select * from SubCategory where category_id = $cat_id order by id ASC", null))
    }

    fun getSubCatId(name: String): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select id from SubCategory where name = \"$name\" ", null))
    }

    fun findGoodName(name: String, as_map: Boolean = true): MutableList<MutableMap<String, String>>{
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select id from Good where name = \"$name\" ", null))

    }

    fun findGoodNameAsArray(name: String): MutableList<MutableList<String>> {
        val db = this.readableDatabase
        return queryToArray(db.rawQuery("select id from Good where name like \"%$name%\" ", null))
    }

    fun findGoodId(): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select id from Good order by id DESC limit 1", null))
    }

    fun addGood(name: String){
        val db = this.writableDatabase
        val a = db.rawQuery("insert into Good (name) values ( \"$name\")", null)
        a.moveToFirst();
        a.close();
    }

    fun addCookingGood(cooking_id: Int, good_id: Int){
        val db = this.writableDatabase
        val a = db.rawQuery("insert into Cooking_good (Cooking_id, Good_id) values ( $cooking_id, $good_id)", null)
        a.moveToFirst();
        a.close();
    }

//    fun findIdForSearch(arr:MutableList<String>): MutableList<MutableList<MutableMap<String, String>>> {
//        val db = this.readableDatabase
//        val id_arr : MutableList<MutableList<MutableMap<String, String>>> = mutableListOf()
//        for (i in arr){
//            id_arr.add(queryToArrayMap(db.rawQuery("select id from Good where name like \"%$i%\"",null)))
//        }
//        return id_arr
////        val db = this.readableDatabase
////        var s : String =""
////        for (i in arr){
////            s += "\""+ i + "\""
////            if (i != arr[arr.size-1]){ s += " OR "}
////        }
////        Log.d("ddd", s)
////        return queryToArray(db.rawQuery("SELECT id FROM Good WHERE CONTAINS(name, \'$s\') ",null))
//    }

//    fun searchCook(list : String, amount : Int): MutableList<MutableMap<String, String>> {
//        val db = this.readableDatabase
//        Log.d("ddd", amount.toString())
//        val b = queryToArrayMap(db.rawQuery("select Cooking_id, count(Good_id) from Cooking_good where Good_id in ($list) group by Cooking_id having count(Good_id)  >= $amount",null))
//
//        return b
//    }

    fun searchRecipe(list: String): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        var idStr : String = ""
        idStr= list.replace("[", "(")
        idStr = idStr.replace("]", ")")
        return queryToArrayMap(db.rawQuery("select * from Recipe where Cooking_id in $idStr", null))
    }

    fun searchRecipeName(n: String): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select * from Recipe where name like \"%$n%\"", null))
    }

    fun addShopingList(name: String){
        val db = this.writableDatabase
        val a = db.rawQuery("insert into Shopping_list_good (Good_name) values (\"$name\") ", null)
        a.moveToFirst();
        a.close();
    }

    fun getShoppingList(): MutableList<MutableMap<String, String>> {
        val db = this.readableDatabase
        return queryToArrayMap(db.rawQuery("select * from Shopping_list_good", null))
    }

    fun delShoppingList(name: String){
        val db = this.writableDatabase
        val a = db.rawQuery("delete from Shopping_list_good where Good_name = \"$name\" ", null)
        a.moveToFirst();
        a.close();
    }

    fun getCookByGood(good_id: List<String>): MutableList<MutableList<String>> {
        val db = this.readableDatabase
        var idStr = good_id.toString()
        idStr = idStr.replace("[", "(")
        idStr = idStr.replace("]", ")")
        return queryToArray(db.rawQuery("select Cooking_id from Cooking_good where Good_id in $idStr", null))
    }

    fun getCookByGoods(good_id: List<String>, cook_id: List<String>): MutableList<MutableList<String>> {
        val db = this.readableDatabase
        var idStr = good_id.toString()
        var cookStr = cook_id.toString()
        idStr = idStr.replace("[", "(")
        idStr = idStr.replace("]", ")")
        cookStr = cookStr.replace("[", "(")
        cookStr = cookStr.replace("]", ")")
        Log.d("ddd", "select Cooking_id from Cooking_good where (Good_id in $idStr) and (Cooking_id in $cookStr)")
        return queryToArray(db.rawQuery("select Cooking_id from Cooking_good where (Good_id in $idStr) and (Cooking_id in $cookStr)", null))
    }

    fun searchByGoods(goods: MutableList<String>): List<String> {
        var goods_id = findGoodNameAsArray(goods[0]).flatten()
        var cook_arr : List<String> = getCookByGood(goods_id).flatten()
        if(goods.size > 1)
            for(goodName in goods.subList(1, goods.size)) {
                goods_id = findGoodNameAsArray(goodName).flatten()
                cook_arr = getCookByGoods(goods_id, cook_arr).flatten()
            }
        return cook_arr
    }

   fun getAmountProduct(name: String): MutableList<MutableMap<String, String>> {
       val db = this.readableDatabase
       return queryToArrayMap(db.rawQuery("select * from Measure where product = \"$name\"", null))
    }

    //    fun addGoods()

}
