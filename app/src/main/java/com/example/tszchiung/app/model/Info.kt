package com.example.tszchiung.app.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


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

    constructor(username: String?, nationality: String?,
                email: String?, prefer: String?, gender: String?,
                major: String?, year: String?, last: String?,
                first: String?, bio: String?, ext: String?) {
        this.username = username
        this.nationality = nationality
        this.email = email
        this.prefer = prefer
        this.gender = gender
        this.major = major
        this.year = year
        this.last = last
        this.first = first
        this.bio = bio
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