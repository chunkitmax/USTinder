package com.example.tszchiung.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.Toast
import com.example.tszchiung.app.model.InfoWithoutExt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    private val IMAGE_REQUEST_CODE = 1

    private var mDatabase: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        // Layout
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        var lp = bg.layoutParams as ViewGroup.MarginLayoutParams
        lp.setMargins(lp.leftMargin, lp.topMargin-resources.getDimensionPixelSize(resId), lp.rightMargin, lp.bottomMargin)
        bg.layoutParams = lp
        StatusBarUtil.setTranslucentForImageView(this, 70, bg)

        FirebaseAuth.getInstance()!!.addAuthStateListener {
            val user = it.currentUser
            if (user != null) {
                confirm.setOnClickListener {
                    if (validateForm()) {
                        InfoWithoutExt()
                        val userDbRef = FirebaseDatabase.getInstance().reference
                                .child("users")
                                .child(user.uid)
                        userDbRef.addValueEventListener(object: ValueEventListener {
                            override fun onCancelled(error: DatabaseError?) {
                                Toast.makeText(this@AboutActivity, error!!.message, Toast.LENGTH_SHORT).show()
                            }
                            override fun onDataChange(snapshot: DataSnapshot?) {
                                val info = snapshot!!.getValue(InfoWithoutExt::class.java)!!
                                info.first = first_name.text.toString()
                                info.last = last_name.text.toString()
                                info.prefer = prefered_name.text.toString()
                                info.gender = gender.text.toString()
                                info.year = year_of_study.text.toString()
                                info.major = major.text.toString()
                                info.nationality = nationality.text.toString()
                                info.bio = about_me.text.toString()
                                userDbRef.setValue(info)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                startActivityForResult(Intent(this@AboutActivity, ImageActivity::class.java), IMAGE_REQUEST_CODE)
                                            } else {
                                                finishWithStatus(it.exception!!.message!!)
                                            }
                                        }
                            }
                        })
                    }
                }
            } else {
                finishWithStatus(false)
            }
        }

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
    }

    private fun validateForm(): Boolean {
        if (first_name.text.isNullOrEmpty()) {
            Toast.makeText(this, "Enter your last name!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (last_name.text.isNullOrEmpty()) {
            Toast.makeText(this, "Enter your first name!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (prefered_name.text.isNullOrEmpty()) {
            Toast.makeText(this, "Enter your preferred name!", Toast.LENGTH_SHORT).show()
            return false
        }
        gender.setText(gender.text.toString().toUpperCase())
        if (gender.text.isNullOrEmpty() || listOf("M", "F").indexOf(gender.text.toString()) < 0) {
            Toast.makeText(this, "Enter your gender!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (year_of_study.text.isNullOrEmpty()) {
            Toast.makeText(this, "Enter your year of study!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (major.text.isNullOrEmpty()) {
            Toast.makeText(this, "Enter your major!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (nationality.text.isNullOrEmpty()) {
            Toast.makeText(this, "Enter your nationality!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (about_me.text.isNullOrEmpty()) {
            Toast.makeText(this, "Enter your bio!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun finishWithStatus(reason: String) {
        finishWithStatus(false, reason)
    }

    fun finishWithStatus(success: Boolean=true, reason: String?=null) {
        assert(success && reason == null)
        val result = Intent()
        if (!success) {
            result.putExtra("reason", reason)
            setResult(0, result)
        }
        else
            setResult(1, result)
        finish()
    }
}