package com.example.tszchiung.app.model

import com.google.firebase.database.IgnoreExtraProperties
import android.R.attr.author
import com.google.firebase.database.Exclude



@IgnoreExtraProperties
class Info {
    var username: String? = null
    var nationality: String? = null
    var email: String? = null
    var prefer: String? = null
    var gender: String? = null
    var major: String? = null
    var year: String? = null
    var last: String? = null
    var first: String? = null
    var bio: String? = null
    var ext: String? = null

    constructor() {
        // default constructor
    }

    constructor(_username: String?, nationality: String?,
                _email: String?, _prefer: String?, _gender: String?,
                _major: String?, _year: String?, _last: String?,
                _first: String?, _bio: String?, ext: String?) {
        this.username = _username
        this.nationality = nationality
        this.email = _email
        this.prefer = _prefer
        this.gender = _gender
        this.major = _major
        this.year = _year
        this.last = _last
        this.first = _first
        this.bio = _bio
        this.ext = ext
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("username", username!!)
        result.put("nationality", nationality!!)
        result.put("email", email!!)
        result.put("prefer", prefer!!)
        result.put("gender", gender!!)
        result.put("major", major!!)
        result.put("year", year!!)
        result.put("last", last!!)
        result.put("first", first!!)
        result.put("bio", bio!!)
        result.put("ext", ext!!)

        return result
    }

}