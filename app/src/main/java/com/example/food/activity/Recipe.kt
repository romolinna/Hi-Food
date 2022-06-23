package com.example.food.activity

import android.content.Context
import android.content.Intent
import android.graphics.ColorSpace.Model
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.food.R
import com.example.food.bd.DbHelper
import com.example.food.model.Adapter
import com.example.food.model.AdapterProduct
import com.example.food.model.ModelProduct
import java.lang.Character.getName


class Recipe:AppCompatActivity(), AdapterProduct.Listener {
    private var flag : Boolean = true
    val productCheck: MutableList<String> = mutableListOf()
    private lateinit var timerBox : LinearLayout
    private lateinit var editMin : EditText
    private lateinit var editSec : EditText
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var timeSec : TextView
    private lateinit var timeMin : TextView
    private var timeResumeMin : Long = 0
    private var timeResumeSec : Long = 0
    private var timerRunning : Boolean = false
    private lateinit var timer : CountDownTimer
    private lateinit var sound : SoundPool
    private var db = DbHelper(this)
    val items: MutableList<ModelProduct> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.product_item_recipe)

        setContentView(R.layout.recipe)
        var image : ImageView = findViewById(R.id.image_recipe)
        var name : TextView = findViewById(R.id.name_recipe)
        val btnFav : ImageButton = findViewById(R.id.btn_fav_recipe)
        val listProduct : RecyclerView = findViewById(R.id.list_product_recipe)
        var cookText : TextView = findViewById(R.id.cook_text)
        val noteEdit : EditText = findViewById(R.id.note_edit)
        val noteBtnEdit : ImageButton = findViewById(R.id.note_edit_btn)
        val noteBtnDel : ImageButton = findViewById(R.id.note_del_btn)
        editMin = findViewById(R.id.edit_min)
        editSec = findViewById(R.id.edit_sec)
        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)
        timerBox = findViewById(R.id.timer)
        timeMin = findViewById(R.id.time_min)
        timeSec = findViewById(R.id.time_sec)

        supportActionBar?.title = Html.fromHtml("<font color=\"black\" face=\"Poiret One\"><i>" + getString(R.string.app_name) + "</i></font>")

        if(isOfic() == 0) {
            btnFav.visibility = View.GONE
        }

        timerBox.visibility = View.GONE
        getFavorite()
        changeButton(btnFav)
        setImage(image)
        setName(name)
        noteEdit.setText(getNote())
        for (i in getCook()){cookText.append(i)}

        val arr: MutableList<String> = getGoods()

        val adapter = AdapterProduct(this)
        listProduct.adapter = adapter
        listProduct.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        fillItems(arr);
        adapter.loadItems(items);

        sound = SoundPool(1, AudioManager.STREAM_ALARM, 100)
        sound.load(this, R.raw.timer, 1)

        btnStart.setOnClickListener {
            if (timerRunning){
                pauseTimer()
            }else{
                val minutes = if (editMin.text.isNullOrEmpty()) timeResumeMin   else editMin.text.toString().toLong()
                val seconds = if (editSec.text.isNullOrEmpty()) timeResumeSec   else editSec.text.toString().toLong()

                timerBox.visibility = View.VISIBLE

                editMin.isEnabled = false
                editSec.isEnabled = false

                startTimer(minutes, seconds)
                editMin.setText("")
                editSec.setText("")
            }
        }

        btnStop.setOnClickListener {
            stopTimer()
        }
        updateUi(0, 0)

        noteBtnDel.setImageResource(R.drawable.del)
        noteBtnEdit.setImageResource(R.drawable.save_note)

        noteBtnDel.setOnClickListener {
            this.db.setNote(getCooking_id(), "")
            noteEdit.setText("")
        }
        noteBtnEdit.setOnClickListener {
            this.db.setNote(getCooking_id(), noteEdit.text.toString())
        }

        btnFav.setOnClickListener{
            flag = !flag
            changeButton(btnFav)
            changeFav()
        }
    }

    private fun changeButton(btn: ImageButton){
        if(isOfic() == 1){
            if (flag){
                btn.setImageResource(R.drawable.star_on)
            }else{
                btn.setImageResource(R.drawable.star_off)
            }
        }
    }

    private fun changeFav(){
        if (flag){
            this.db.setIsFav(getID())
        }else{
            this.db.setIsNotFav(getID())
        }
    }

    private fun startTimer(min: Long, sec: Long){

        val startValue = min * 1000 * 60 + sec * 1000
        timerRunning = true
        btnStart.text = "Пауза"
        timer = object : CountDownTimer(startValue, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secUntilFinished = millisUntilFinished / 1000
                timeResumeMin = secUntilFinished / 60
                timeResumeSec = secUntilFinished - 60 * timeResumeMin
                updateUi(timeResumeMin, timeResumeSec)
            }
            override fun onFinish() {
                updateUi(0, 0)
                btnStart.text = "Старт"
                timerRunning = false
                timerBox.visibility = View.GONE
                editMin.isEnabled = true
                editSec.isEnabled = true
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val curVolume: Float = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
                val maxVolume: Float = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
                val leftVolume = curVolume / maxVolume
                val rightVolume = curVolume / maxVolume
                sound.play(1, leftVolume, rightVolume, 1, 0, 1f)
            }
        }
        timer.start()
    }

    private fun pauseTimer(){
        timer.cancel()
        timerRunning = false
        btnStart.text = "Старт"
    }
    private fun stopTimer(){
        timeResumeMin = 0
        timeResumeSec = 0
        timer.cancel()
        updateUi(timeResumeMin, timeResumeSec)
        timerRunning = false
        btnStart.text = "Старт"
        timerBox.visibility = View.GONE
        editMin.isEnabled = true
        editSec.isEnabled = true
    }


    private fun updateUi(min: Long, sec: Long){
        timeMin.text = min.toString()
        timeSec.text = sec.toString()
    }

    fun setImage(image: ImageView){
        val img = intent.getSerializableExtra("image").toString()
        if (img.isEmpty()){
            image.setImageResource(R.drawable.image)
        }else{
            image.setImageDrawable(Drawable.createFromPath("/data/data/com.example.food/images/" + img))
//            if (isOfic == "0"){
//                image.setImageURI(Uri.parse(img))
//            }else{
//                val ims: InputStream = applicationContext.assets.open(img)
//                val d = Drawable.createFromStream(ims, null)
//                image.setImageDrawable(d)
            }
        }
//        val img = intent.getSerializableExtra("image").toString()
//        if (img.isEmpty()){
//            val ims: InputStream = assets.open("images/Category/1_alcoholic.jpg")
//            val d = Drawable.createFromStream(ims, null)
//            image.setImageDrawable(d)
//        }else {
//            image.setImageResource(R.drawable.image)
//        }

    fun setName(textView: TextView){
        val name = intent.getSerializableExtra("name").toString()
        textView.text = name
    }

    fun getFavorite(){
        var is_fav = intent.getSerializableExtra("is_favorite").toString()
        if(is_fav == "0"){flag = false}
        if(is_fav == "1"){flag = true}
    }

    fun getCooking_id(): Int {
        return intent.getSerializableExtra("Cooking_id").toString().toInt()
    }

    fun getCook(): MutableList<String> {
        val arr = this.db.getCooking(getCooking_id())
        val cook : MutableList<String> = mutableListOf()
        for (i in arr){cook.add(i.getValue("cooking_text"))}
        return cook
    }

    fun getGoods(): MutableList<String> {
        val goods =  this.db.getGoods(getCooking_id())
        var arr : MutableList<String> = mutableListOf()
        for (i in goods){
            for((key, value) in i){arr.add(value)}
        }
        return arr
    }

    fun getID(): Int {
        return intent.getSerializableExtra("id").toString().toInt()
    }

    fun getNote(): String {
        val arr = this.db.getNote(getCooking_id())
        var note : String = ""
        for (i in arr){note = i.getValue("note")}
        return note
    }

    fun isOfic(): Int {
        Log.d("ddd", this.db.getOfficial(getID()).toString())
        val r = this.db.getOfficial(getID())
        var isOfficial : Int = 10
        for(i in r){isOfficial = i.getValue("is_official").toInt()}
        return isOfficial
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

    override fun onStop() {
        super.onStop()
        for(i in productCheck){this.db.addShopingList(i)}

    }

}