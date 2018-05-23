package com.example.tszchiung.app

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.tszchiung.app.model.Info
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jaeger.library.StatusBarUtil
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(), ValueEventListener, FirebaseAuth.AuthStateListener {

    private var REQUEST_CODE: Int = -1

    private var mStorage: StorageReference? = null
    private var mDatabase: DatabaseReference? = null

    private lateinit var userId: String
    private lateinit var userObject: Info

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setHomeAsUpIndicator(this.getDrawable(R.drawable.abc_ic_ab_back_material))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Layout
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        var lp = blur_picture.layoutParams as ViewGroup.MarginLayoutParams
        lp.setMargins(lp.leftMargin, lp.topMargin-resources.getDimensionPixelSize(resId), lp.rightMargin, lp.bottomMargin)
        blur_picture.layoutParams = lp
        StatusBarUtil.setTranslucentForImageView(this, 70, blur_picture)
        lp = toolbar.layoutParams as ViewGroup.MarginLayoutParams
        lp.setMargins(lp.leftMargin, lp.topMargin+resources.getDimensionPixelSize(resId), lp.rightMargin, lp.bottomMargin)
        toolbar.layoutParams = lp

        FirebaseAuth.getInstance()!!.addAuthStateListener(this)
    }
    override fun onAuthStateChanged(it: FirebaseAuth) {
        if (it.currentUser!!.displayName.isNullOrEmpty()) {
            finishWithStatus("Please logged in again.")
        } else {
            mDatabase = FirebaseDatabase.getInstance().reference
            mStorage = FirebaseStorage.getInstance().reference

            userId = it.currentUser!!.uid
            getUserInfo()
        }
        it.removeAuthStateListener(this)
    }

    private fun getUserInfo() {
        mDatabase!!
                .child("users")
                .child(userId)
                .addListenerForSingleValueEvent(this)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finishWithStatus(true)
        return true
    }

    override fun onCancelled(error: DatabaseError?) {
        finishWithStatus(error!!.message)
    }
    override fun onDataChange(snapshot: DataSnapshot?) {
        userId = snapshot!!.key

        val target = object: SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                Blurry.with(this@ProfileActivity).radius(20).from(resource).into(blur_picture)
                profile_pic.setImageBitmap(getCroppedBitmap(resource))
            }
        }

        userObject = snapshot.getValue(Info::class.java)!!
        prefered_name.text = userObject.prefer.orEmpty()
        nationality.setText(userObject.nationality.orEmpty(), TextView.BufferType.EDITABLE)
        about_me.setText(userObject.bio.orEmpty(), TextView.BufferType.EDITABLE)
        year_of_study.setText(userObject.year.orEmpty(), TextView.BufferType.EDITABLE)
        major.setText(userObject.major.orEmpty(), TextView.BufferType.EDITABLE)
        if (userObject.gender.isNullOrEmpty()) {
            gender_group.check(R.id.male)
        } else {
            when (userObject.gender) {
                "M" -> gender_group.check(R.id.male)
                "F" -> gender_group.check(R.id.female)
            }
        }

        mStorage!!.child("images/${userObject.username.orEmpty()}.${userObject.ext.orEmpty()}")
                .downloadUrl
                .addOnSuccessListener { uri ->
                    run {
                        Glide.with(this)
                                .asBitmap()
                                .load(uri)
                                .into(target)
                    }
                }
        progress_bar.visibility = View.GONE
    }

    fun finishWithStatus(reason: String) {
        finishWithStatus(false, reason)
    }
    fun finishWithStatus(success: Boolean=true, reason: String?=null) {
        assert(success && reason == null)
        if (nationality.text.isNotEmpty() &&
                about_me.text.isNotEmpty() &&
                year_of_study.text.isNotEmpty() &&
                major.text.isNotEmpty()) {

            userObject.nationality = nationality.text.toString()
            userObject.bio = about_me.text.toString()
            userObject.year = year_of_study.text.toString()
            userObject.major = major.text.toString()
            userObject.gender = when (gender_group.checkedRadioButtonId) {
                R.id.male -> "M"
                R.id.female -> "F"
                else -> "M"
            }

            mDatabase!!.child("users").child(userId).updateChildren(userObject.toMap())
            mDatabase!!.child("users").child(userId).removeEventListener(this)

            val result = Intent()
            if (!success) {
                result.putExtra("reason", reason)
                setResult(0, result)
            }
            else
                setResult(1, result)
            finish()
        } else {
            Toast.makeText(this, "Please fill in all fields before leave", Toast.LENGTH_LONG).show()
        }
    }

    fun getCroppedBitmap(bitmap: Bitmap): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        if (width > height) {
            width = height
        } else if (width < height) {
            height = width
        }
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = 0xff424242
        val paint = Paint()
        val rect = Rect(0, 0, width, height)
        val srcRect = Rect((bitmap.width-width)/2, (bitmap.height-height)/2,
                bitmap.width-(bitmap.width-width)/2, bitmap.height-(bitmap.height-height)/2)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color.toInt()
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        canvas.drawCircle(width / 2.0f, height / 2.0f,
                width / 2.0f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, srcRect, rect, paint)
        return output
    }
}
