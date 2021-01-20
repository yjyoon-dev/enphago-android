package dev.yjyoon.enphago

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Half.toFloat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()!!.hide()
        CoroutineScope(Dispatchers.Default).launch{
            while(true){
                Thread.sleep(15)
                if(titleLogo.rotationY==-360f) titleLogo.rotationY=360f
                titleLogo.rotationY-=1f
            }
        }
        gameStartBtn.setOnClickListener {
            val intent = Intent(this,ChatActivity::class.java)
            startActivity(intent)
        }
        exitBtn.setOnClickListener {
            finish()
        }
    }
}