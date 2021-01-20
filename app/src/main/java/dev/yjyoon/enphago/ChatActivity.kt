package dev.yjyoon.enphago

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.*

class ChatActivity : AppCompatActivity() {
    private var turn: Int = 0
    private var checkWord = CheckWord()
    private var context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.title = "게임 진행중 - ${turn}턴"

        val adapter = ChatAdapter()
        chatRecyclerView.adapter = adapter
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        ansBtn.setOnClickListener {
            val word = userInput.text.toString()

            CoroutineScope(Dispatchers.Main).launch{
                userInput.setText("")
                val checkResult = checkWord.check(word)

                when (checkResult) {
                    CheckWord.OK -> {
                        adapter.chatList.add(Chat(Chat.USER,word))
                        adapter.notifyDataSetChanged()
                        turn+=1
                        supportActionBar?.title = "게임 진행중 - ${turn}턴"
                    }
                    CheckWord.BLANK_INPUT -> Toast.makeText(context,"단어를 입력해주세요",Toast.LENGTH_SHORT).show()
                    CheckWord.TOO_SHORT -> Toast.makeText(context,"단어가 너무 짧습니다",Toast.LENGTH_SHORT).show()
                    CheckWord.ALREADY_USED -> Toast.makeText(context,"이미 사용된 단어입니다",Toast.LENGTH_SHORT).show()
                    CheckWord.INVALID_WORD -> Toast.makeText(context,"명사가 아니거나 존재하지 않는 단어입니다",Toast.LENGTH_SHORT).show()
                    CheckWord.INTERNET_DISCONNECTED -> Toast.makeText(context,"네트워크 연결 상태를 확인해주세요",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}