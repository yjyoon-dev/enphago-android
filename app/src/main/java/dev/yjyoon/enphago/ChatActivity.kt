package dev.yjyoon.enphago

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class ChatActivity : AppCompatActivity() {
    var turn: Int = 0
    var checkWord = CheckWord()
    val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        getSupportActionBar()?.setTitle("게임 진행중 - ${turn}턴")

        val adapter = ChatAdapter()
        chatRecyclerView.adapter = adapter
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        ansBtn.setOnClickListener {
            val message = userInput.text.toString()
            if(message.isNotEmpty()) {
                userInput.setText("")

                var checkResult = true

                CoroutineScope(Dispatchers.IO).launch {
                    checkResult = checkWord.isValid(message)

                    if(checkResult){
                        adapter.chatList.add(Chat(Chat.USER,message))
                        turn += 1

                        CoroutineScope(Dispatchers.Main).launch {
                            adapter.notifyDataSetChanged()
                            getSupportActionBar()?.setTitle("게임 진행중 - ${turn}턴")
                            ansBtn.setEnabled(false)
                        }

                        Thread.sleep(500)
                        adapter.chatList.add(Chat(Chat.ENPHAGO, message))

                        CoroutineScope(Dispatchers.Main).launch {
                            adapter.notifyDataSetChanged()
                            ansBtn.setEnabled(true)
                        }
                    }
                    else{
                        CoroutineScope(Dispatchers.Main).launch{
                            Toast.makeText(context,"유효하지 않은 단어입니다",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            else{
                Toast.makeText(this,"단어를 입력해주세요",Toast.LENGTH_SHORT).show()
            }
        }
    }
}