package com.example.tszchiung.app.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class User {
    var username: String? = null
    var email: String? = null

    constructor() {
        // default constructor
    }

    constructor(username: String?, email: String?) {
        this.username = username
        this.email = email
    }
}