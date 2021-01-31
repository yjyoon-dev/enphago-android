package dev.yjyoon.enphago

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val animation = ResourcesCompat.getDrawable(
            this.resources,
            R.drawable.gradation_animation,
            null
        ) as AnimationDrawable

        val layout = findViewById<ConstraintLayout>(R.id.infoScreenLayout)
        layout.setBackgroundDrawable(animation)

        animation.setEnterFadeDuration(2000)
        animation.setExitFadeDuration(4000)
        animation.start()

    }
}