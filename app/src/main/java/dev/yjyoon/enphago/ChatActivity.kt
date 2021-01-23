package dev.yjyoon.enphago

import android.content.DialogInterface
import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.*
import java.io.InputStream

class ChatActivity : AppCompatActivity() {
    private var turn = 0
    private val checkWord = CheckWord()
    private val context = this
    private var enphagoWord: String? = "init"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.title = "게임 진행중 - ${turn}턴"

        val adapter = ChatAdapter()
        chatRecyclerView.adapter = adapter
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        ansBtn.setOnClickListener {
            ansBtn.isEnabled = false
            wordCheckProgressBar.isVisible = true

            val word = userInput.text.toString()
            userInput.setText("")

            CoroutineScope(Dispatchers.Main).launch{
                var successed = false

                when (checkWord.check(word,enphagoWord!!)) {
                    CheckWord.OK -> {
                        adapter.chatList.add(Chat(Chat.USER,word))
                        adapter.notifyDataSetChanged()
                        turn+=1
                        supportActionBar?.title = "게임 진행중 - ${turn}턴"
                        successed = true
                    }
                    CheckWord.BLANK_INPUT -> Toast.makeText(context,"단어를 입력해주세요",Toast.LENGTH_SHORT).show()
                    CheckWord.TOO_SHORT -> Toast.makeText(context,"단어가 너무 짧습니다",Toast.LENGTH_SHORT).show()
                    CheckWord.MISMATCHED_WORD -> Toast.makeText(context,"끝말이 이어지지 않습니다",Toast.LENGTH_SHORT).show()
                    CheckWord.ALREADY_USED -> Toast.makeText(context,"이미 사용된 단어입니다",Toast.LENGTH_SHORT).show()
                    CheckWord.INVALID_WORD -> Toast.makeText(context,"명사가 아니거나 존재하지 않는 단어입니다",Toast.LENGTH_SHORT).show()
                    CheckWord.INTERNET_DISCONNECTED -> Toast.makeText(context,"네트워크 연결 상태를 확인해주세요",Toast.LENGTH_SHORT).show()
                }

                ansBtn.isEnabled = true
                wordCheckProgressBar.isVisible = false

                chatRecyclerView.scrollToPosition(adapter.chatList.size-1)

                if(successed) {
                    enphagoWord = null
                    delay(1000) // 자연스러움을 위한 시간차 딜레이

                    val roomWordHelper = Room.databaseBuilder(context, RoomWordHelper::class.java, "word")
                            .allowMainThreadQueries()
                            .build()
                    val candList: MutableList<Word> = roomWordHelper.roomWordDAO().getWord(word.substring(word.length - 1))

                    if (candList.isNotEmpty()) {
                        candList.shuffle()

                        for (cand in candList) {
                            if (!checkWord.usedWordSet.contains(cand.word)) {
                                enphagoWord = cand.word
                                break
                            }
                        }
                    }

                    if (enphagoWord != null) {
                        checkWord.usedWordSet.add(enphagoWord!!)
                        adapter.chatList.add(Chat(Chat.ENPHAGO, enphagoWord!!))
                        adapter.notifyDataSetChanged()
                        chatRecyclerView.scrollToPosition(adapter.chatList.size - 1)
                    }
                    else{
                        adapter.chatList.add(Chat(Chat.ENPHAGO, "..."))
                        adapter.notifyDataSetChanged()
                        chatRecyclerView.scrollToPosition(adapter.chatList.size - 1)

                        delay(1000)

                        val alertDialog = AlertDialog.Builder(context)
                        alertDialog.setTitle("당신의 승리")
                        alertDialog.setMessage("축하합니다!\nEnphago가 기권하였습니다.")
                        alertDialog.setIcon(R.mipmap.app_icon)

                        supportActionBar?.title = "게임 종료 - 총 ${turn}턴"
                        userInput.hint = "게임이 종료되었습니다"
                        userInput.isEnabled = false
                        ansBtn.isEnabled = false

                        alertDialog.setPositiveButton("확인",null)
                        alertDialog.show()
                    }
                }
            }
        }
    }
}