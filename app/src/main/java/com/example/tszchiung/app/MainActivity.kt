package com.example.tszchiung.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.widget.Toast
import android.util.Log
import android.text.TextUtils

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "FirebaseEmailPassword"
    private var mDatabase: DatabaseReference? = null
    //private var mMessageReference: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_email_sign_in.setOnClickListener(this)
        btn_email_create_account.setOnClickListener(this)
        btn_sign_out.setOnClickListener(this)
        btn_verify_email.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()

        mDatabase = FirebaseDatabase.getInstance().reference
        //mMessageReference = FirebaseDatabase.getInstance().getReference("message")

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
        updateUI(currentUser)
    }

    override fun onClick(view: View?) {
        val i = view!!.id

        if (i == R.id.btn_email_create_account) {
            createAccount(edtEmail.text.toString(), edtPassword.text.toString())
        } else if (i == R.id.btn_email_sign_in) {
            signIn(edtEmail.text.toString(), edtPassword.text.toString())
        } else if (i == R.id.btn_sign_out) {
            signOut()
        } else if (i == R.id.btn_verify_email) {
            sendEmailVerification()
        }
    }

    private fun createAccount(email: String, password: String) {
        Log.e(TAG, "createAccount:" + email)
        if (!validateForm(email, password)) {
            return
        }

        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.e(TAG, "createAccount: Success!")

                        // update UI with the signed-in user's information
                        val user = mAuth!!.currentUser
                        updateUI(user)
                        writeNewUser(user!!.uid, getUsernameFromEmail(user.email), user.email)
                    } else {
                        Log.e(TAG, "createAccount: Fail!", task.exception)
                        Toast.makeText(applicationContext, "Authentication failed!", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
    }

    private fun signIn(email: String, password: String) {
        Log.e(TAG, "signIn:" + email)
        if (!validateForm(email, password)) {
            return
        }

        mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.e(TAG, "signIn: Success!")

                        // update UI with the signed-in user's information
                        val user = mAuth!!.getCurrentUser()
                        if(user!!.isEmailVerified) {
                            val intent = Intent(this, AboutActivity::class.java)
                            startActivity(intent)
                        } else {
                            updateUI(user)
                        }
                    } else {
                        Log.e(TAG, "signIn: Fail!", task.exception)
                        Toast.makeText(applicationContext, "Authentication failed!", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }

                    if (!task.isSuccessful) {
                        tvStatus.text = "Authentication failed!"
                    }
                }
    }

    private fun signOut() {
        mAuth!!.signOut()
        updateUI(null)
    }

    private fun sendEmailVerification() {
        // Disable Verify Email button
        findViewById<View>(R.id.btn_verify_email).isEnabled = false

        val user = mAuth!!.currentUser
        user!!.sendEmailVerification()
                .addOnCompleteListener(this) { task ->
                    // Re-enable Verify Email button
                    findViewById<View>(R.id.btn_verify_email).isEnabled = true

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

    private fun updateUI(user: FirebaseUser?) {

        if (user != null) {
            tvStatus.text = "User Email: " + user.email + " (Verified: " + user.isEmailVerified + ")"
            tvDetail.text = "User ID: " + user.uid

            email_password_buttons.visibility = View.GONE
            email_password_fields.visibility = View.GONE
            layout_signed_in_buttons.visibility = View.VISIBLE

            btn_verify_email.isEnabled = !user.isEmailVerified
        } else {
            tvStatus.text = "Sign Up or Sign in"
            tvDetail.text = null

            email_password_buttons.visibility = View.VISIBLE
            email_password_fields.visibility = View.VISIBLE
            layout_signed_in_buttons.visibility = View.GONE
        }
    }

    private fun writeNewUser(userId: String, username: String?, email: String?) {
        val user = User(username, email)

        FirebaseDatabase.getInstance().reference.child("users").child(userId).setValue(user)
    }

    private fun getUsernameFromEmail(email: String?): String {
        return if (email!!.contains("@")) {
            email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            email
        }
    }
}