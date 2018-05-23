package com.example.tszchiung.app.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class InfoWithoutExt {
    var username: String? = null
    var email: String? = null
    var prefer: String? = null
    var gender: String? = null
    var major: String? = null
    var year: String? = null
    var last: String? = null
    var first: String? = null
    var bio: String? = null
    var nationality: String? = null

    constructor() {
        // default constructor
    }

    constructor(email: String?, username: String?) {
        this.email = email
        this.username = username
    }

    constructor(username: String?, email: String?,
                prefer: String?, gender: String?,
                major: String?, year: String?,
                last: String?, first: String?,
                bio: String?, nationality: String?) {
        this.username = username
        this.email = email
        this.prefer = prefer
        this.gender = gender
        this.major = major
        this.year = year
        this.last = last
        this.first = first
        this.bio = bio
        this.nationality = nationality
    }
}