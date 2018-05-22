package com.example.tszchiung.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.tszchiung.app.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "FirebaseEmailPassword"
    private var mDatabase: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sign_in.setOnClickListener(this)
        sign_up.setOnClickListener(this)

        // Layout
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        var lp = bg.layoutParams as ViewGroup.MarginLayoutParams
        lp.setMargins(lp.leftMargin, lp.topMargin-resources.getDimensionPixelSize(resId), lp.rightMargin, lp.bottomMargin)
        bg.layoutParams = lp
        StatusBarUtil.setTranslucentForImageView(this, 70, bg)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        /*
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl

            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project.
            val uid = user.uid
        }
        */
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth!!.currentUser
//        updateUI(currentUser)
    }

    override fun onClick(view: View?) {
        val i = view!!.id

        when (i) {
            R.id.sign_up -> run {
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.placeholder, HomeFragment.newInstance(false))
                ft.addToBackStack("SignUp")
                ft.commit()
            }
            R.id.sign_in -> signIn(username_email.text.toString(), password.text.toString())
//            R.id.btn_sign_out -> signOut()
//            R.id.btn_verify_email -> sendEmailVerification()
        }
    }

    private fun createAccount(email: String, password: String) {
        Log.e(TAG, "createAccount:$email")
        if (!validateForm(email, password)) {
            return
        }
        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.e(TAG, "createAccount: Success!")

                        val user = mAuth!!.currentUser
//                        updateUI(user)
                        writeNewUser(user!!.uid, getUsernameFromEmail(user.email), user.email)
                    } else {
                        Log.e(TAG, "createAccount: Fail!", task.exception)
                        Toast.makeText(applicationContext, "Authentication failed!", Toast.LENGTH_SHORT).show()
//                        updateUI(null)
                    }
                }
    }

    private fun signIn(email: String, password: String) {
        Log.e(TAG, "signIn:$email")
        if (!validateForm(email, password)) {
            return
        }
        mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.e(TAG, "signIn: Success!")

                        val user = mAuth!!.getCurrentUser()
                        if(user!!.isEmailVerified) {
                            val intent = Intent(this, AboutActivity::class.java)
                            startActivity(intent)
                        } else {
//                            updateUI(user)
                        }
                    } else {
                        Log.e(TAG, "signIn: Fail!", task.exception)
                        Toast.makeText(this, "Invalid Username/Email or Password", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun signOut() {
        mAuth!!.signOut()
//        updateUI(null)
    }

    private fun sendEmailVerification() {
//        findViewById<View>(R.id.btn_verify_email).isEnabled = false

        val user = mAuth!!.currentUser
        user!!.sendEmailVerification()
                .addOnCompleteListener(this) { task ->
//                    findViewById<View>(R.id.btn_verify_email).isEnabled = true
//
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Verification email sent to " + user.email!!, Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e(TAG, "sendEmailVerification failed!", task.exception)
                        Toast.makeText(applicationContext, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun validateForm(email: String, password: String): Boolean {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Enter email address!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Enter password!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(applicationContext, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

//    private fun updateUI(user: FirebaseUser?) {
//
//        if (user != null) {
//            tvStatus.text = "User Email: " + user.email + " (Verified: " + user.isEmailVerified + ")"
//            tvDetail.text = "User ID: " + user.uid
//
//            email_password_buttons.visibility = View.GONE
//            email_password_fields.visibility = View.GONE
//            layout_signed_in_buttons.visibility = View.VISIBLE
//
//            btn_verify_email.isEnabled = !user.isEmailVerified
//        } else {
//            tvStatus.text = "Sign Up or Sign in"
//            tvDetail.text = null
//
//            email_password_buttons.visibility = View.VISIBLE
//            email_password_fields.visibility = View.VISIBLE
//            layout_signed_in_buttons.visibility = View.GONE
//        }
//    }

    private fun writeNewUser(userId: String, username: String?, email: String?) {
        val user = User(username, email)
        mDatabase!!.child("users").child(userId).setValue(user)
    }

    private fun getUsernameFromEmail(email: String?): String {
        return if (email!!.contains("@")) {
            email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            email
        }
    }
}