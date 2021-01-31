package dev.yjyoon.enphago

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.enphago_chat.view.*
import kotlinx.android.synthetic.main.user_chat.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.RuntimeException

class ChatAdapter(context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var context: Context? = null
    init {
        this.context = context
    }

    var chatList = mutableListOf<Chat>()

    override fun getItemViewType(position: Int): Int {
        val chat = chatList[position]
        return chat.writer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?
        return when (viewType) {
            Chat.USER -> {
                view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.user_chat, parent, false)
                UserHolder(view)
            }
            Chat.ENPHAGO -> {
                view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.enphago_chat, parent, false)
                EnphagoHolder(view)
            }
            else -> throw RuntimeException("Unknown View Type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = chatList[position]
        when (chat.writer) {
            Chat.USER -> (holder as UserHolder).setChat(chat)
            Chat.ENPHAGO -> (holder as EnphagoHolder).setChat(chat)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    inner class UserHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var word: String? = null
        init {
            itemView.userText.setOnClickListener{
                if(word!="..."){
                    CoroutineScope(Dispatchers.Main).launch {
                        itemView.searchUserProgressBar.isVisible = true
                        val searchResult = SearchWord().search(word!!)
                        if(searchResult != null){
                            val resultDialog = AlertDialog.Builder(context!!)
                            resultDialog.setTitle(searchResult.word)
                            resultDialog.setMessage("[${searchResult.pos}]\n${searchResult.def}")
                            resultDialog.setIcon(R.drawable.enphago_icon)
                            resultDialog.setPositiveButton("확인",null)
                            resultDialog.show()
                        }
                        else Toast.makeText(context,"네트워크 연결 상태를 확인해주세요", Toast.LENGTH_SHORT).show()
                        itemView.searchUserProgressBar.isVisible = false
                    }
                }
            }
        }
        fun setChat(chat: Chat) {
            word = chat.message
            itemView.userText.text = "  ${word}  "
        }
    }

    inner class EnphagoHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var word: String? = null
        init {
            itemView.enphagoText.setOnClickListener{
                if(word!="..." && word!="도 있었는데 아쉽군요"){
                    CoroutineScope(Dispatchers.Main).launch {
                        itemView.searchEnphagoProgressBar.isVisible = true
                        val searchResult = SearchWord().search(word!!)
                        if(searchResult != null){
                            val resultDialog = AlertDialog.Builder(context!!)
                            resultDialog.setTitle(searchResult.word)
                            resultDialog.setMessage("[${searchResult.pos}]\n${searchResult.def}")
                            resultDialog.setIcon(R.drawable.enphago_icon)
                            resultDialog.setPositiveButton("확인",null)
                            resultDialog.show()
                        }
                        else Toast.makeText(context,"네트워크 연결 상태를 확인해주세요", Toast.LENGTH_SHORT).show()
                        itemView.searchEnphagoProgressBar.isVisible = false
                    }
                }
            }
        }
        fun setChat(chat: Chat) {
            word = chat.message
            itemView.enphagoText.text = "  ${word}  "
        }
    }
}