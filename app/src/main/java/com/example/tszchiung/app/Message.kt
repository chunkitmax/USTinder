package com.example.tszchiung.app

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.Exclude

@IgnoreExtraProperties
class Message {

    var author: String? = ""
    var body: String? = ""
    var time: String? = ""

    constructor() {
        // Default constructor
    }

    constructor(author: String, body: String, time: String) {
        this.author = author
        this.body = body
        this.time = time
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("author", author!!)
        result.put("body", body!!)
        result.put("time", time!!)

        return result
    }
}