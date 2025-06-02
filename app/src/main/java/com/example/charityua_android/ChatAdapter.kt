package com.example.charityua_android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(
    private val messages: List<ChatMessage>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_DATE_HEADER = 0
        private const val TYPE_MESSAGE = 1
    }

    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val organizerLayout: View = view.findViewById(R.id.organizerMessageLayout)
        val organizerName: TextView = view.findViewById(R.id.organizerName)
        val organizerMessage: TextView = view.findViewById(R.id.organizerMessage)
        val organizerTime: TextView = view.findViewById(R.id.organizerTime)

        val donorLayout: View = view.findViewById(R.id.donorMessageLayout)
        val donorName: TextView = view.findViewById(R.id.donorName)
        val donorMessage: TextView = view.findViewById(R.id.donorMessage)
        val donorTime: TextView = view.findViewById(R.id.donorTime)
    }

    inner class DateHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.findViewById(R.id.dateHeader)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isDateHeader) TYPE_DATE_HEADER else TYPE_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_DATE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_date_header, parent, false)
            DateHeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message, parent, false)
            MessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        if (holder is DateHeaderViewHolder) {
            holder.dateText.text = message.text
        } else if (holder is MessageViewHolder) {
            if (message.isFromOrganizer) {
                holder.organizerLayout.visibility = View.VISIBLE
                holder.donorLayout.visibility = View.GONE

                holder.organizerName.text = message.senderName
                holder.organizerMessage.text = message.text
                holder.organizerTime.text = message.time // лише час
            } else {
                holder.donorLayout.visibility = View.VISIBLE
                holder.organizerLayout.visibility = View.GONE

                holder.donorName.text = message.senderName
                holder.donorMessage.text = message.text
                holder.donorTime.text = message.time // лише час
            }
        }
    }

    override fun getItemCount(): Int = messages.size
}