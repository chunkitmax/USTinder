package com.example.tszchiung.app.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.tszchiung.app.R

class Friend {
    var name: String
    val uid: String
    var imageUri: Uri?

    constructor(name: String, uid: String, imageUri: Uri?=null) {
        this.name = name
        this.uid = uid
        this.imageUri = imageUri
    }
}

class FriendViewHolder: RecyclerView.ViewHolder {
    var name: TextView
    var image: ImageView

    constructor(view: View) : super(view) {
        this.name = view.findViewById(R.id.name)
        this.image = view.findViewById(R.id.image)
    }
}

class FriendAdapter(val context: Context, private val friends: List<Friend>) : RecyclerView.Adapter<FriendViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.friend_list_entry, parent, false)
        return FriendViewHolder(view)
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.name.text = friends[position].name
        try {
            Glide.with(context).load(friends[position].imageUri).into(holder.image)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return
    }
}