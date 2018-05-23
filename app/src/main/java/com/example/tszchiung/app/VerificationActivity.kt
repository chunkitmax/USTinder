package com.example.tszchiung.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_verification.*

class VerificationActivity : AppCompatActivity() {

    private val ABOUT_REQUEST_CODE = 1

    var checkTimer = Handler()
    lateinit var checking: Runnable
    var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
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

        checking = Runnable {
            currentUser!!.reload().addOnCompleteListener {
                if (currentUser!!.isEmailVerified) {
//                    finishWithStatus(true)
                    startActivityForResult(Intent(this, AboutActivity::class.java), ABOUT_REQUEST_CODE)
                } else {
                    checkTimer.postDelayed(checking, 5000)
                }
            }
        }

        val restartTimer = Runnable {
            checkTimer.removeCallbacks(checking)
            checkTimer.postDelayed(checking, 5000)
        }

        FirebaseAuth.getInstance().addAuthStateListener {
            currentUser = it.currentUser
            if (currentUser != null) {
                sendVerificationEmail(restartTimer)
            } else {
                finishWithStatus(false,"Cannot get current user!")
            }
        }

        send_again.setOnClickListener {
            sendVerificationEmail(restartTimer)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ABOUT_REQUEST_CODE -> run {
                when {
                    resultCode == 1 -> finishWithStatus(true)
                    data != null -> finishWithStatus(data.extras.getString("reason"))
                    else -> finishWithStatus(false)
                }
            }
        }
    }

    private fun sendVerificationEmail(onSuccessRunnable: Runnable) {
        currentUser!!.sendEmailVerification()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Verification email sent to " + currentUser!!.email!!, Toast.LENGTH_SHORT).show()
                        onSuccessRunnable.run()
                    } else {
                        Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    override fun onBackPressed() {}

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
