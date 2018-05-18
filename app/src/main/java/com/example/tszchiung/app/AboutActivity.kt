package com.example.tszchiung.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    private var mDatabase: DatabaseReference? = null
    private var mMessageReference: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        btn_about_sign_out.setOnClickListener {
            finish()
        }

        btn_about_send.setOnClickListener {
            val user = mAuth!!.currentUser
            val userid = user!!.uid
            val username = getUsernameFromEmail(user.email)
            val email = user.email

            if(validateForm(_prefer.text.toString(), _gender.text.toString(), _major.text.toString(),
                    _year.text.toString(), _last.text.toString(), _first.text.toString(), _bio.text.toString())) {

                val info = Info(username, email, _prefer.text.toString(), _gender.text.toString(), _major.text.toString(),
                        _year.text.toString(), _last.text.toString(), _first.text.toString(), _bio.text.toString())

                FirebaseDatabase.getInstance().reference.child("users").child(userid).setValue(info)
                val intent = Intent(this, ImageActivity::class.java)
                startActivity(intent)
            }
        }

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mMessageReference = FirebaseDatabase.getInstance().getReference("users")
    }

    private fun getUsernameFromEmail(email: String?): String {
        return if (email!!.contains("@")) {
            email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            email
        }
    }

    private fun validateForm(prefer: String?, gender: String?, major: String?, year: String?,
                             last: String?, first: String?, bio: String?): Boolean {

        if (TextUtils.isEmpty(last)) {
            Toast.makeText(applicationContext, "Enter your last name!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(first)) {
            Toast.makeText(applicationContext, "Enter your first name!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(prefer)) {
            Toast.makeText(applicationContext, "Enter your preferred name!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(gender)) {
            Toast.makeText(applicationContext, "Enter your gender!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(year)) {
            Toast.makeText(applicationContext, "Enter your year of study!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(major)) {
            Toast.makeText(applicationContext, "Enter your major!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(bio)) {
            Toast.makeText(applicationContext, "Enter your bio!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}