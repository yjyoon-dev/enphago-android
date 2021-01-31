package dev.yjyoon.enphago

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
    val adapter = ChatAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setSupportActionBar(findViewById(R.id.customToolbar))
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.outline_help_outline_white_36)

        customToolbar.turnText.text = "첫 단어를 입력해주세요!"

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

                        successed = true
                    }
                    CheckWord.BLANK_INPUT -> Toast.makeText(context,"단어를 입력해주세요",Toast.LENGTH_SHORT).show()
                    CheckWord.TOO_SHORT -> Toast.makeText(context,"두 글자 이상의 단어를 입력해주세요",Toast.LENGTH_SHORT).show()
                    CheckWord.MISMATCHED_WORD -> Toast.makeText(context,"\"${enphagoWord}\"와(과) 끝말이 이어지지 않습니다",Toast.LENGTH_SHORT).show()
                    CheckWord.ALREADY_USED -> Toast.makeText(context,"\"${word}\"은(는) 이미 사용된 단어입니다",Toast.LENGTH_SHORT).show()
                    CheckWord.INVALID_WORD -> Toast.makeText(context,"명사가 아니거나 존재하지 않는 단어입니다",Toast.LENGTH_SHORT).show()
                    CheckWord.INTERNET_DISCONNECTED -> Toast.makeText(context,"네트워크 연결 상태를 확인해주세요",Toast.LENGTH_SHORT).show()
                }

                ansBtn.isEnabled = true
                wordCheckProgressBar.isVisible = false

                chatRecyclerView.scrollToPosition(adapter.chatList.size-1)

                // 엔파고 반격 시작
                if(successed) {
                    delay(1000) // 자연스러움을 위한 시간차 딜레이

                    enphagoWord = getAnswer(word)

                    if (enphagoWord != null) {
                        checkWord.usedWordSet.add(enphagoWord!!)
                        adapter.chatList.add(Chat(Chat.ENPHAGO, enphagoWord!!))
                        adapter.notifyDataSetChanged()
                        chatRecyclerView.scrollToPosition(adapter.chatList.size - 1)
                    }
                    else {
                        if(turn > 0){
                            delay(1000)

                            adapter.chatList.add(Chat(Chat.ENPHAGO, "..."))
                            adapter.notifyDataSetChanged()
                            chatRecyclerView.scrollToPosition(adapter.chatList.size - 1)

                            delay(1000)

                            finishGame(1)

                            val alertDialog = AlertDialog.Builder(context)
                            alertDialog.setTitle("당신의 승리")
                            alertDialog.setMessage("축하합니다!\nEnphago가 기권하였습니다.")
                            alertDialog.setIcon(R.mipmap.app_icon)

                            alertDialog.setPositiveButton("확인",null)
                            alertDialog.show()
                        }
                        else{
                            val alertDialog = AlertDialog.Builder(context)
                            alertDialog.setTitle("규칙 위반")
                            alertDialog.setMessage("첫 턴부터 한 방 단어를 사용할 수 없습니다. 다시 입력해주세요.")
                            alertDialog.setIcon(R.mipmap.app_icon)

                            alertDialog.setPositiveButton("확인",null)
                            alertDialog.show()

                            checkWord.usedWordSet.remove(word)
                            enphagoWord = "init"
                            turn-=1
                        }
                    }
                }
                turn+=1
                if(turn > 0) customToolbar.turnText.text = "${turn}턴 진행 중"
            }
        }
    }

    fun getAnswer(word: String): String? {
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
                    return cand.word
                }
            }
        }

        return null
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
                            CoroutineScope(Dispatchers.Main).launch {
                                adapter.chatList.add(Chat(Chat.USER, "..."))
                                adapter.notifyDataSetChanged()
                                chatRecyclerView.scrollToPosition(adapter.chatList.size - 1)

                                val hint = getAnswer(enphagoWord!!)
                                if(hint != null) {
                                    delay(1000)
                                    adapter.chatList.add(Chat(Chat.ENPHAGO, "${hint}"))
                                    adapter.notifyDataSetChanged()
                                    chatRecyclerView.scrollToPosition(adapter.chatList.size - 1)
                                    delay(1000)
                                    adapter.chatList.add(Chat(Chat.ENPHAGO, "도 있었는데 아쉽군요"))
                                    adapter.notifyDataSetChanged()
                                    chatRecyclerView.scrollToPosition(adapter.chatList.size - 1)
                                    delay(1000)
                                }

                                finishGame(0)

                                val loseDialog = AlertDialog.Builder(context)
                                loseDialog.setTitle("당신의 패배")
                                loseDialog.setMessage("Enphago로부터 기권하셨습니다.")
                                loseDialog.setIcon(R.mipmap.app_icon)
                                loseDialog.setPositiveButton("확인",null)
                                loseDialog.show()
                            }
                        }
                    }
                }
                alertDialog.setPositiveButton("예",dialogListener)
                alertDialog.setNegativeButton("아니오",null)
                alertDialog.show()
            }
            android.R.id.home -> {
                val intent = Intent(this,InfoActivity::class.java)
                startActivity(intent)
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
                        finishGame(0)

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

    fun finishGame(result: Int) {
        findViewById<View>(R.id.surrenderBtn).isVisible = false
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        customToolbar.turnText.text = "총 ${turn}턴 종료"
        userInput.hint = "게임이 종료되었습니다"
        userInput.isEnabled = false
        ansBtn.isEnabled = false
        isFinished = true
        val pref = getSharedPreferences("record", Context.MODE_PRIVATE)
        val maxTurn = pref.getInt("maxTurn",0)
        if(maxTurn < turn) {
            val prefEditor = pref.edit()
            prefEditor.putInt("maxTurn",turn)
            prefEditor.apply()
        }
        if(result==1) recordWin()
        else recordLose()
    }

    fun recordWin() {
        val pref = getSharedPreferences("record", Context.MODE_PRIVATE)
        val win = pref.getInt("win",0)
        val prefEditor = pref.edit()
        prefEditor.putInt("win",win+1)
        prefEditor.apply()
    }

    fun recordLose() {
        val pref = getSharedPreferences("record", Context.MODE_PRIVATE)
        val lose = pref.getInt("lose",0)
        val prefEditor = pref.edit()
        prefEditor.putInt("lose",lose+1)
        prefEditor.apply()
    }
}