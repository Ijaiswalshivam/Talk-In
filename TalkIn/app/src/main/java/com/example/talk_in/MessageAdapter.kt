package com.example.talk_in

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private val context: Context, private var messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_RECEIVE = 1
    private val ITEM_SENT = 2
    private val ITEM_DATE = 3

    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    fun updateList(newList: ArrayList<Message>) {
        messageList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            ITEM_RECEIVE -> {
                val view = inflater.inflate(R.layout.receive, parent, false)
                ReceiveViewHolder(view)
            }
            ITEM_SENT -> {
                val view = inflater.inflate(R.layout.sent, parent, false)
                SentViewHolder(view)
            }
            ITEM_DATE -> {
                val view = inflater.inflate(R.layout.date_section, parent, false)
                DateViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        if (currentMessage.isDateSection) {
            val dateHolder = holder as DateViewHolder
            dateHolder.dateTextView.text = currentMessage.message
        } else {
            val formattedTime = timeFormat.format(Date(currentMessage.timestamp ?: 0L))

            when (holder) {
                is SentViewHolder -> {
                    if (!currentMessage.isDeleted) {
                        holder.sentMessage.text = AESUtils.decrypt(currentMessage.message.toString())
                        holder.sendTimestamp.text = formattedTime

                        holder.sentMessage.setOnClickListener {
                            (context as? ChatActivity)?.showContextMenu(holder.sentMessage, currentMessage)
                        }
                    } else {
                        holder.sentMessage.text = "You deleted the message"
                        holder.sendTimestamp.text = formattedTime
                        holder.sentMessage.setOnClickListener(null)
                    }
                }
                is ReceiveViewHolder -> {
                    if (!currentMessage.isDeleted) {
                        holder.receiveMessage.text = AESUtils.decrypt(currentMessage.message.toString())
                        holder.receiveTimestamp.text = formattedTime

                        holder.receiveMessage.setOnClickListener {
                            (context as? ChatActivity)?.showContextMenu(holder.receiveMessage, currentMessage)
                        }
                    } else {
                        holder.receiveMessage.text = "This message was deleted"
                        holder.receiveTimestamp.text = formattedTime
                        holder.receiveMessage.setOnClickListener(null)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return when {
            currentMessage.isDateSection -> ITEM_DATE
            FirebaseAuth.getInstance().currentUser?.uid == currentMessage.senderId -> ITEM_SENT
            else -> ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int = messageList.size

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage: TextView = itemView.findViewById(R.id.txt_sent_message)
        val sendTimestamp: TextView = itemView.findViewById(R.id.send_timestamp)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage: TextView = itemView.findViewById(R.id.txt_receive_message)
        val receiveTimestamp: TextView = itemView.findViewById(R.id.received_timestamp)
    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.date_section_text)
    }
}