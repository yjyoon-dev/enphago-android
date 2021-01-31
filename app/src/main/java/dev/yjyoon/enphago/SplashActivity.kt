package dev.yjyoon.enphago

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import kotlin.concurrent.thread

class SplashActivity : AppCompatActivity() {
    val context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)

        val pref = getSharedPreferences("first_launch", Context.MODE_PRIVATE)
        if(!pref.getBoolean("check",false)){

            val TOTAL_WORD_NUM = 50000
            wordLoadProgressBar.max = TOTAL_WORD_NUM
            wordLoadProgressBar.isVisible = true
            wordLoadText.isVisible = true

            CoroutineScope(Dispatchers.IO).launch{
                val roomWordHelper = Room.databaseBuilder(context, RoomWordHelper::class.java, "word").allowMainThreadQueries().build()
                roomWordHelper.roomWordDAO().deleteAll()
                val assetManager: AssetManager = resources.assets
                val inputStream: InputStream = assetManager.open("word_list.txt")
                inputStream.bufferedReader().readLines().forEach {
                    val word = Word(it.substring(0..0),it)
                    roomWordHelper.roomWordDAO().insert(word)
                    wordLoadProgressBar.incrementProgressBy(2)
                }

                val editor = pref.edit()
                editor.putBoolean("check",true)
                editor.apply()

                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
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