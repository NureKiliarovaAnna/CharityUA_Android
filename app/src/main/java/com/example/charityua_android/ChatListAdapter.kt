package com.example.charityua_android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatListAdapter(
    private val chats: List<ChatSummary>,
    private val onChatClick: (ChatSummary) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fundraiserTitle: TextView = view.findViewById(R.id.chatFundraiserTitle)
        val lastMessage: TextView = view.findViewById(R.id.chatLastMessage)
        val unreadCount: TextView = view.findViewById(R.id.chatUnreadCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]
        holder.fundraiserTitle.text = chat.fundraiserTitle
        holder.lastMessage.text = chat.lastMessage

        if (chat.unreadCount > 0) {
            holder.unreadCount.visibility = View.VISIBLE
            holder.unreadCount.text = chat.unreadCount.toString()
        } else {
            holder.unreadCount.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onChatClick(chat)
        }
    }

    override fun getItemCount(): Int = chats.size
}