package com.example.tszchiung.app.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.tszchiung.app.R

class Partner {
    var name: String
    var info: String
    var email: String
    var imageUri: Uri?

    constructor(name: String, info: String, email: String, imageUri: Uri?=null) {
        this.name = name
        this.info = info
        this.email = email
        this.imageUri = imageUri
    }
}

class CardAdapter(context: Context) : ArrayAdapter<Partner>(context, 0) {

    class ViewHolder {
        var name: TextView
        var info: TextView
        var email: String? = null
        var image: ImageView
        constructor(view: View) {
            this.name = view.findViewById(R.id.name)
            this.info = view.findViewById(R.id.info)
            this.image = view.findViewById(R.id.image)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder: ViewHolder
        var contentView: View? = null

        if (convertView == null) {
            val li = LayoutInflater.from(context)
            contentView = li.inflate(R.layout.partner_card, parent, false)
            holder = ViewHolder(contentView)
            contentView.tag = holder
        } else {
            holder = contentView!!.tag as ViewHolder
        }
        // Continue here
        var p: Partner = getItem(position)
        holder.name.text = p.name
        holder.info.text = p.info
        holder.email = p.email
        try {
            Glide.with(context).load(p.imageUri).into(holder.image)
        } catch(e: IllegalArgumentException) {}

        return contentView!!
    }
}