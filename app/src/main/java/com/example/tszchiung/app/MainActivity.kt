package com.example.tszchiung.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.tszchiung.app.model.Info
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener, FirebaseAuth.AuthStateListener {

    private val SIGNUP_REQUEST_CODE = 1
    private val VERIFICATION_REQUEST_CODE = 2
    private val ABOUT_REQUEST_CODE = 3
    private val IMAGE_REQUEST_CODE = 4

    private val TAG = "FirebaseEmailPassword"
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStorage: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sign_in.setOnClickListener(this)
        sign_up.setOnClickListener(this)

        // Layout
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val lp = bg.layoutParams as ViewGroup.MarginLayoutParams
        lp.setMargins(lp.leftMargin, lp.topMargin-resources.getDimensionPixelSize(resId), lp.rightMargin, lp.bottomMargin)
        bg.layoutParams = lp
        StatusBarUtil.setTranslucentForImageView(this, 70, bg)

        FirebaseAuth.getInstance()!!.addAuthStateListener(this)
    }

    override fun onAuthStateChanged(it: FirebaseAuth) {
        mAuth = it
        mDatabase = FirebaseDatabase.getInstance().reference
        mStorage = FirebaseStorage.getInstance().reference
        if (it.currentUser != null) {
            // Logged in before
            checkAccountAndGo()
        }
        mAuth.removeAuthStateListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 1) {
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            Toast.makeText(this, data!!.extras.getString("reason"), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(view: View?) {
        val i = view!!.id

        when (i) {
            R.id.sign_up -> run {
                startActivityForResult(Intent(this, SignupActivity::class.java), SIGNUP_REQUEST_CODE)
            }
            R.id.sign_in -> signIn()
        }
    }

    private fun signIn() {
        if (!validateForm()) {
            return
        }
        var email = username_email.text.toString()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email += "@connect.ust.hk"
        }
        mAuth.signInWithEmailAndPassword(email, password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        checkAccountAndGo()
                    } else {
                        Toast.makeText(this, "Invalid Username/Email or Password", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun checkAccountAndGo() {
        // check if email is verified
        if (!mAuth.currentUser!!.isEmailVerified) {
            username_email.setText("")
            password.setText("")
            startActivityForResult(Intent(this, VerificationActivity::class.java), VERIFICATION_REQUEST_CODE)
            return
        }
        // check if user provided basic info
        mDatabase.child("users").child(mAuth.currentUser!!.uid)
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError?) {
                        Toast.makeText(this@MainActivity, error!!.message, Toast.LENGTH_SHORT).show()
                    }
                    override fun onDataChange(snapshot: DataSnapshot?) {
                        val info = snapshot!!.getValue(Info::class.java)!!
                        if (info.bio.isNullOrEmpty() || info.year.isNullOrEmpty() ||
                                info.major.isNullOrEmpty() || info.gender.isNullOrEmpty()) {
                            username_email.setText("")
                            password.setText("")
                            startActivityForResult(Intent(this@MainActivity, AboutActivity::class.java), ABOUT_REQUEST_CODE)
                        } else {
                            mStorage.child("images").child("${info.username}.${info.ext}")
                                    .downloadUrl
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            username_email.setText("")
                                            password.setText("")
                                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                                        } else {
                                            startActivityForResult(Intent(this@MainActivity, ImageActivity::class.java), IMAGE_REQUEST_CODE)
                                        }
                                    }
                        }
                    }
                })
    }

    private fun validateForm(): Boolean {

        username_email.setText(username_email.text.toString().trim())
        if (username_email.text.isNullOrEmpty()) {
            Toast.makeText(this, "Enter email address!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.text.isNullOrEmpty()) {
            Toast.makeText(this, "Enter password!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.text.toString().length < 6) {
            Toast.makeText(this, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}