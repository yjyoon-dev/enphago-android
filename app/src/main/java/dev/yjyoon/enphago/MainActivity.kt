package dev.yjyoon.enphago

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.TransitionDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Half.toFloat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(Dispatchers.Default).launch{
            while(true){
                Thread.sleep(15)
                if(titleLogo.rotationY==-360f) titleLogo.rotationY=360f
                titleLogo.rotationY-=1f
            }
        }

        val animation = ResourcesCompat.getDrawable(
                this.resources,
                R.drawable.gradation_animation,
                null
        ) as AnimationDrawable

        val layout = findViewById<ConstraintLayout>(R.id.mainScreenLayout)
        layout.setBackgroundDrawable(animation)

        animation.setEnterFadeDuration(2000)
        animation.setExitFadeDuration(4000)
        animation.start()

        gameStartBtn.setOnClickListener {
            val intent = Intent(this,ChatActivity::class.java)
            startActivity(intent)
        }

        recordBtn.setOnClickListener {
            val intent = Intent(this,RecordActivity::class.java)
            startActivity(intent)
        }

        exitBtn.setOnClickListener {
            finish()
        }
    }
}