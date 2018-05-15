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
    var imageUri: Uri

    constructor(name: String, info: String, imageUri: Uri) {
        this.name = name
        this.info = info
        this.imageUri = imageUri
    }
}

class CardAdapter(context: Context) : ArrayAdapter<Partner>(context, 0) {

    class ViewHolder {
        public var name: TextView
        public var info: TextView
        public var image: ImageView
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
        Glide.with(context).load(p.imageUri).into(holder.image)

        return contentView!!
    }
}