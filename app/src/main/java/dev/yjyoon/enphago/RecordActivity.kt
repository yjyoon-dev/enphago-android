package dev.yjyoon.enphago

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_record.*

class RecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        val animation = ResourcesCompat.getDrawable(
            this.resources,
            R.drawable.gradation_animation,
            null
        ) as AnimationDrawable

        val layout = findViewById<ConstraintLayout>(R.id.recordScreenLayout)
        layout.setBackgroundDrawable(animation)

        animation.setEnterFadeDuration(2000)
        animation.setExitFadeDuration(4000)
        animation.start()

        val pref = getSharedPreferences("record", Context.MODE_PRIVATE)
        val win = pref.getInt("win",0)
        val lose = pref.getInt("lose",0)
        val maxTurn = pref.getInt("maxTurn",0)
        val total = win+lose
        var winRate: Double
        if(total==0) winRate = 0.0
        else winRate = win/total*100.0

        totalGameText.text = "총 ${total}판 중"
        recordText.text = "${win}승  ${lose}패"
        winrateText.text = "승률: ${winRate}%"
        maxTurnText.text = "최대 진행 턴 수: ${maxTurn}턴"
    }
}