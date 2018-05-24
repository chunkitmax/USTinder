package com.example.tszchiung.app.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.tszchiung.app.R


class Chat {
    val uid: String
    var msg: String
    var fromMe: Boolean

    constructor(uid: String, msg: String, fromMe: Boolean) {
        this.uid = uid
        this.msg = msg
        this.fromMe = fromMe
    }
}

class ChatViewHolder: RecyclerView.ViewHolder {
    var partner: LinearLayout
    var me: LinearLayout
    var partnerMsg: TextView
    var myMsg: TextView
    var image: ImageView

    constructor(view: View) : super(view) {
        this.partner = view.findViewById(R.id.partner)
        this.me = view.findViewById(R.id.me)
        this.partnerMsg = view.findViewById(R.id.partner_msg)
        this.myMsg = view.findViewById(R.id.my_msg)
        this.image = view.findViewById(R.id.image)
    }
}

class ChatAdapter(val context: Context, private val messages: List<Chat>) : RecyclerView.Adapter<ChatViewHolder>() {
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
        return
    }
}
