package com.example.tszchiung.app.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Message {

    var body: String? = ""
    var time: String? = ""

    constructor() {
        // Default constructor
    }

    constructor(body: String, time: String) {
        this.body = body
        this.time = time
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("body", body!!)
        result.put("time", time!!)

        return result
    }
}