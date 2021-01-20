package dev.yjyoon.enphago

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.enphago_chat.view.*
import kotlinx.android.synthetic.main.user_chat.view.*
import java.lang.RuntimeException

class ChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var chatList = mutableListOf<Chat>()

    override fun getItemViewType(position: Int): Int {
        val chat = chatList.get(position)
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
        val chat = chatList.get(position)
        when (chat.writer) {
            Chat.USER -> (holder as UserHolder).setChat(chat)
            Chat.ENPHAGO -> (holder as EnphagoHolder).setChat(chat)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    inner class UserHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun setChat(chat: Chat) {
            itemView.userText.text = "  ${chat.message}  "
        }
    }

    inner class EnphagoHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun setChat(chat: Chat) {
            itemView.enphagoText.text = "  ${chat.message}  "
        }
    }
}