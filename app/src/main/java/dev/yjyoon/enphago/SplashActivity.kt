package dev.yjyoon.enphago

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.room.Room
import java.io.InputStream
import kotlin.concurrent.thread

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)

        val pref = getSharedPreferences("first_launch", Context.MODE_PRIVATE)
        if(!pref.getBoolean("check",false)){

            val roomWordHelper = Room.databaseBuilder(this, RoomWordHelper::class.java, "word")
                .allowMainThreadQueries()
                .build()
            roomWordHelper.roomWordDAO().deleteAll()
            val assetManager: AssetManager = resources.assets
            val inputStream: InputStream = assetManager.open("word_list.txt")
            inputStream.bufferedReader().readLines().forEach {
                val word = Word(it.substring(0..0),it)
                roomWordHelper.roomWordDAO().insert(word)
            }

            val editor = pref.edit()
            editor.putBoolean("check",true)
            editor.apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            thread(start=true) {
                Thread.sleep(2000)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}