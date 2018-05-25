package com.example.tszchiung.app.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.tszchiung.app.R


class Message {
    val time: String
    var msg: String
    var fromMe: Boolean

    constructor(time: String, msg: String, fromMe: Boolean) {
        this.time = time
        this.msg = msg
        this.fromMe = fromMe
    }
}

class ChatViewHolder: RecyclerView.ViewHolder {
    var partner: LinearLayout
    var me: LinearLayout
    var partnerMsg: TextView
    var myMsg: TextView

    constructor(view: View) : super(view) {
        this.partner = view.findViewById(R.id.partner)
        this.me = view.findViewById(R.id.me)
        this.partnerMsg = view.findViewById(R.id.partner_msg)
        this.myMsg = view.findViewById(R.id.my_msg)
    }
}

class ChatAdapter(val context: Context, private var messages: List<Message>) : RecyclerView.Adapter<ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_msg, parent, false)
        return ChatViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val curMessage = messages[position]
        if (curMessage.fromMe) {
            holder.partner.visibility = View.GONE
            holder.myMsg.text = curMessage.msg
        } else {
            holder.me.visibility = View.GONE
            holder.partnerMsg.text = curMessage.msg
        }
    }

    fun addAll(messages: List<Message>, fromMe: Boolean) {
        if (messages.isNotEmpty()) {
            this.messages = (this.messages.filter {
                it.fromMe != fromMe
            } + messages).sortedBy {
                it.time
            }
            super.notifyDataSetChanged()
        }
    }
}
