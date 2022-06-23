package com.example.food.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.food.R
import com.example.food.bd.DbHelper
import com.example.food.databinding.AddRecipeBinding
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class AddRecipe : AppCompatActivity() {
    private lateinit var binding: AddRecipeBinding
    private val GALLERY_REQUEST = 1;
    var imageUri : Uri? = null
    private var db = DbHelper(this)
    var selectedSubCat: String = ""
    var selectedCat: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = Html.fromHtml("<font color=\"black\" face=\"Poiret One\"><i>" + getString(R.string.app_name) + "</i></font>")

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        var arr: MutableList<String> = mutableListOf()
        val adapterProduct = ArrayAdapter(applicationContext, R.layout.product_item, arr)
        binding.listProductAdd.adapter = adapterProduct

        val catList = getCat()
        val adapterCategory = ArrayAdapter(applicationContext, R.layout.product_item, catList)
        binding.catSpinner.adapter = adapterCategory

        var subCatList = mutableListOf("Подкатегория")
        val adapterSubcategory = ArrayAdapter(applicationContext, R.layout.product_item, subCatList)
        binding.subCatSpinner.adapter = adapterSubcategory

        binding.catSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedCat = binding.catSpinner.selectedItem.toString()
                subCatList = getSubCat(getCatId(selectedCat))
                val adapterSubcategory = ArrayAdapter(applicationContext, R.layout.product_item, subCatList)
                binding.subCatSpinner.adapter = adapterSubcategory
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        binding.subCatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedSubCat = binding.subCatSpinner.selectedItem.toString()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        binding.productAddBtn.setOnClickListener {
            val product = binding.productAdd.text.toString()
            if (product != ""){
                arr.add(binding.productAdd.text.toString())
                binding.productAdd.setText("")
            }
            adapterProduct.notifyDataSetChanged()
        }

        binding.imageAdd.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, GALLERY_REQUEST)
        }

        binding.btnSaveRecipe.setOnClickListener {
            val cook = binding.cookTextAdd.text.toString()
            this.db.addCooking(cook)
            if(imageUri?.let { it1 -> getNameFromUri(it1) }.isNullOrEmpty()){
                this.db.addRecipe(getSubCatId(selectedSubCat), getCookId(), "", binding.nameAdd.text.toString())
            }else{
                imageUri?.let { it1 -> getNameFromUri(it1) }?.let { it2 -> this.db.addRecipe(getSubCatId(selectedSubCat), getCookId(), it2, binding.nameAdd.text.toString()) }
            }
            for (i in arr){
                val a = this.db.findGoodName(i)
                if (a.isNullOrEmpty()){
                    this.db.addGood(i)
                    val p = this.db.findGoodId()
                    var idGood : Int = 0
                    for(i in p){idGood = i.getValue("id").toInt()}
                    this.db.addCookingGood(getCookId(), idGood)
                }else{
                    var idGood : Int = 0
                    for(i in a){idGood = i.getValue("id").toInt()}
                    this.db.addCookingGood(getCookId(), idGood)
                }
            }
            startActivity(Intent(this, MyRecipe::class.java))
            imageUri?.let { it1 -> createFileFromContentUri(it1) }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getNameFromUri(img: Uri): String{
        var fileName: String = ""
        img.let { returnUri ->
            applicationContext.contentResolver.query(returnUri,null,null,null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }
        return fileName
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createFileFromContentUri(fileUri : Uri) : File{
        var fileName : String = ""

        fileUri.let { returnUri ->
            applicationContext.contentResolver.query(returnUri,null,null,null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }

        val fileType: String? = fileUri.let { returnUri ->
            contentResolver.getType(returnUri)
        }

        val iStream : InputStream = contentResolver.openInputStream(fileUri)!!
        val outputDir : File = File("/data/data/com.example.food/images/")
        val outputFile : File = File(outputDir, fileName)
        copyStreamToFile(iStream, outputFile)
        iStream.close()
        return  outputFile
    }

    fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }

//    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        Log.d("aaaa", resultCode.toString())
//        Log.d("aaaa", requestCode.toString())
        when (requestCode) {
            GALLERY_REQUEST -> if (resultCode == RESULT_OK ) {
//            GALLERY_REQUEST -> if (resultCode == RESULT_OK && requestCode == GALLERY_REQUEST) {
                imageUri = data!!.data
//                Log.d("aaaa", imageUri.toString())
//
//                Log.d("aaacf", applicationContext.contentResolver.persistedUriPermissions.toString())
                binding.imageAdd.setImageURI(imageUri)
            }
        }
    }

//    @RequiresApi(Build.VERSION_CODES.KITKAT)
//    override fun onDestroy() {
//        Log.d("aaac", imageUri.toString())
//        Log.d("aaac", applicationContext.contentResolver.takePersistableUriPermission(Uri.parse(imageUri.toString()), Intent.FLAG_GRANT_READ_URI_PERMISSION).toString())
//        super.onDestroy()
//    }

    fun getCat(): MutableList<String> {
        val a = this.db.getCategories(1)
        val arr :MutableList<String> = mutableListOf()
        for (i in a) {
            arr.add(i.getValue("name"))
        }
        return arr
    }

    fun getCatId(name: String): Int {
        val id = this.db.getCatId(name)
        var num : Int = 0
        for (i in id){num = i.getValue("id").toInt()}
        return num
    }

    fun getSubCat(id_cat: Int): MutableList<String> {
        val subcat = this.db.getAllSubCat(id_cat)
        var arr : MutableList<String> = mutableListOf()
        for(i in subcat){arr.add(i.getValue("name"))}
        return arr
    }

    fun getCookId(): Int {
        val idIn = this.db.findCooking()
        var idOut:Int = 0
        for(i in idIn){idOut = i.getValue("id").toInt()}
        return idOut
    }

    fun getSubCatId(name: String): Int {
        val id = this.db.getSubCatId(name)
        var num : Int = 0
        for (i in id){num = i.getValue("id").toInt()}
        return num
    }
}

