package com.example.tszchiung.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.ViewGroup
import android.widget.Toast
import com.example.tszchiung.app.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_signup.*


class SignupActivity : AppCompatActivity() {

    private var VERIFICATION_REQUEST_CODE: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setHomeAsUpIndicator(this.getDrawable(R.drawable.abc_ic_ab_back_material))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Layout
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        var lp = bg.layoutParams as ViewGroup.MarginLayoutParams
        lp.setMargins(lp.leftMargin, lp.topMargin-resources.getDimensionPixelSize(resId), lp.rightMargin, lp.bottomMargin)
        bg.layoutParams = lp
        StatusBarUtil.setTranslucentForImageView(this, 70, bg)
        lp = toolbar.layoutParams as ViewGroup.MarginLayoutParams
        lp.setMargins(lp.leftMargin, lp.topMargin+resources.getDimensionPixelSize(resId), lp.rightMargin, lp.bottomMargin)
        toolbar.layoutParams = lp

        sign_up.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        if (!validateForm()) {
            return
        }
        FirebaseAuth.getInstance()!!
                .createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = task.result.user
                        val userObj = User(username.text.toString(), user.email)
                        FirebaseDatabase.getInstance()!!.reference
                                .child("users")
                                .child(user!!.uid)
                                .setValue(userObj)
                        val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(username.text.toString())
                                .build()
                        task.result.user.updateProfile(profileUpdates)
                                .addOnCompleteListener {
                                    if (task.isSuccessful) {
                                        startActivityForResult(Intent(this, VerificationActivity::class.java), VERIFICATION_REQUEST_CODE)
                                    } else {
                                        finishWithStatus(task.exception!!.message!!)
                                    }
                                }
                    } else {
                        Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun validateForm(): Boolean {
        if (username.text.isNullOrEmpty()) {
            Toast.makeText(this, "Enter username!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.text.isNullOrEmpty()) {
            Toast.makeText(this, "Enter password!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.text.length < 6) {
            Toast.makeText(this, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (email.text.isNullOrEmpty() ||
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()) {
            Toast.makeText(this, "Enter valid email address!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            VERIFICATION_REQUEST_CODE -> run {
                when {
                    resultCode == 1 -> finishWithStatus(true)
                    data != null -> finishWithStatus(data.extras.getString("reason"))
                    else -> finishWithStatus(false)
                }
            }
        }
    }

    override fun onBackPressed() {
        finishWithStatus(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        finishWithStatus(false)
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
