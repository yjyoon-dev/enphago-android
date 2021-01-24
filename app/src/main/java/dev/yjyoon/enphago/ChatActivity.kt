package dev.yjyoon.enphago

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.view.*
import kotlinx.coroutines.*

class ChatActivity : AppCompatActivity() {
    private var turn = 0
    private val checkWord = CheckWord()
    private val context = this
    private var enphagoWord: String? = "init"
    private var isFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setSupportActionBar(findViewById(R.id.customToolbar))
        val toolbar = supportActionBar
        toolbar!!.setDisplayShowCustomEnabled(true)
        toolbar!!.setDisplayShowTitleEnabled(false)
        toolbar!!.setDisplayHomeAsUpEnabled(true)
        toolbar!!.setHomeAsUpIndicator(R.drawable.outline_help_outline_white_36)
        customToolbar.turnText.text = "${turn}턴 진행 중"

        val adapter = ChatAdapter()
        chatRecyclerView.adapter = adapter
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        ansBtn.setOnClickListener {

            // 플레이어 단어 검사 시작
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
                        customToolbar.turnText.text = "${turn}턴 진행 중"
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

                // 엔파고 반격 시작
                if(successed) {
                    enphagoWord = null
                    delay(1000) // 자연스러움을 위한 시간차 딜레이

                    var tail = word.substring(word.length - 1)
                    if(checkWord.convertMap.containsKey(tail)) tail = checkWord.convertMap.get(tail)!!

                    val roomWordHelper = Room.databaseBuilder(context, RoomWordHelper::class.java, "word")
                            .allowMainThreadQueries()
                            .build()
                    val candList: MutableList<Word> = roomWordHelper.roomWordDAO().getWord(tail)

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
                    else {
                        delay(1000)

                        adapter.chatList.add(Chat(Chat.ENPHAGO, "..."))
                        adapter.notifyDataSetChanged()
                        chatRecyclerView.scrollToPosition(adapter.chatList.size - 1)

                        delay(1000)

                        val alertDialog = AlertDialog.Builder(context)
                        alertDialog.setTitle("당신의 승리")
                        alertDialog.setMessage("축하합니다!\nEnphago가 기권하였습니다.")
                        alertDialog.setIcon(R.mipmap.app_icon)

                        finishGame()

                        alertDialog.setPositiveButton("확인",null)
                        alertDialog.show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.surrender, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.surrenderBtn -> {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle("기권하시겠습니까?")
                alertDialog.setMessage("기권하시면 패배 처리됩니다.")

                val dialogListener = DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            finishGame()
                            val loseDialog = AlertDialog.Builder(context)
                            loseDialog.setTitle("당신의 패배")
                            loseDialog.setMessage("Enphago로부터 기권하셨습니다.")
                            loseDialog.setIcon(R.mipmap.app_icon)
                            loseDialog.setPositiveButton("확인",null)
                            loseDialog.show()
                        }
                    }
                }
                alertDialog.setPositiveButton("예",dialogListener)
                alertDialog.setNegativeButton("아니오",null)
                alertDialog.show()
            }
            android.R.id.home -> {
                Toast.makeText(context,"도움말 표시",Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    override fun onBackPressed() {
        if(!isFinished) {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle("게임에서 나가시겠습니까?")
            alertDialog.setMessage("게임에서 나가시면 기권 처리됩니다.")

            val dialogListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        finishGame()
                        val loseDialog = AlertDialog.Builder(context)
                        loseDialog.setTitle("당신의 패배")
                        loseDialog.setMessage("게임 이탈로 인해 기권처리 되었습니다.")
                        loseDialog.setIcon(R.mipmap.app_icon)
                        loseDialog.setPositiveButton("확인") { _, _ -> finish() }
                        loseDialog.show()
                    }
                }
            }
            alertDialog.setPositiveButton("예",dialogListener)
            alertDialog.setNegativeButton("아니오",null)
            alertDialog.show()
        }
        else super.onBackPressed()
    }

    fun finishGame() {
        findViewById<View>(R.id.surrenderBtn).isVisible = false
        customToolbar.turnText.text = "총 ${turn}턴 종료"
        userInput.hint = "게임이 종료되었습니다"
        userInput.isEnabled = false
        ansBtn.isEnabled = false
        isFinished = true
    }
}