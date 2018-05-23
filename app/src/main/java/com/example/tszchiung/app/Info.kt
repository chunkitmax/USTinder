package com.example.tszchiung.app

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Info {
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

    constructor(_username: String?, _email: String?,
                _prefer: String?, _gender: String?,
                _major: String?, _year: String?,
                _last: String?, _first: String?,
                _bio: String?, _nationality: String?) {
        this.username = _username
        this.email = _email
        this.prefer = _prefer
        this.gender = _gender
        this.major = _major
        this.year = _year
        this.last = _last
        this.first = _first
        this.bio = _bio
        this.nationality = _nationality
    }
}