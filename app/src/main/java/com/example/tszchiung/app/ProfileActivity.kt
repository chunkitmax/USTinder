package com.example.tszchiung.app

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jaeger.library.StatusBarUtil
import jp.wasabeef.blurry.Blurry

class ProfileActivity : AppCompatActivity(), ValueEventListener {

    private var REQUEST_CODE: Int = -1

    private var mStorage: StorageReference? = null
    private var mDatabase: DatabaseReference? = null

    private lateinit var progressBar: ProgressBar
    private lateinit var blurPic: ImageView
    private lateinit var profilePic: ImageView
    private lateinit var preferedName: TextView

    var target_username: String? = null
    var user_obj: Object? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setHomeAsUpIndicator(this.getDrawable(R.drawable.abc_ic_ab_back_material))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        progressBar = findViewById(R.id.progress_bar)
        blurPic = findViewById(R.id.blur_picture)
        profilePic = findViewById(R.id.profile_pic)
        preferedName = findViewById(R.id.prefered_name)

        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        var lp = blurPic.layoutParams as ViewGroup.MarginLayoutParams
        lp.setMargins(lp.leftMargin, lp.topMargin-resources.getDimensionPixelSize(resId), lp.rightMargin, lp.bottomMargin)
        blurPic.layoutParams = lp
        StatusBarUtil.setTranslucentForImageView(this, 70, blurPic)
        lp = toolbar.layoutParams as ViewGroup.MarginLayoutParams
        lp.setMargins(lp.leftMargin, lp.topMargin+resources.getDimensionPixelSize(resId), lp.rightMargin, lp.bottomMargin)
        toolbar.layoutParams = lp

        target_username = intent.extras!!.getString("username", "jackngtszchiu")
        REQUEST_CODE = intent.extras!!.getInt("request_code", -1)
        assert(REQUEST_CODE >= 0)


        FirebaseAuth.getInstance()!!.signInWithEmailAndPassword("cklauah@connect.ust.hk", "123456")
                .addOnCompleteListener { task ->
                    run {
                        if (task.isSuccessful) {
                            mDatabase = FirebaseDatabase.getInstance().reference
                            mStorage = FirebaseStorage.getInstance().reference

                            getUserInfo(target_username!!)
                        } else {
                            finishWithStatus("Firebase authentication failed")
                        }
                    }
                }
    }

    override fun onSupportNavigateUp(): Boolean {
        finishWithStatus()
        return true
    }

    private fun getUserInfo(username: String) {
        mDatabase!!
                .child("users")
                .orderByChild("username")
                .equalTo(username)
                .addValueEventListener(this)
    }

    override fun onCancelled(error: DatabaseError?) {
        finishWithStatus(error!!.message)
    }
    override fun onDataChange(snapshot: DataSnapshot?) {
        if (snapshot!!.childrenCount > 1) {
            finishWithStatus("There must not be two users with same username")
        }
        for (child: DataSnapshot in snapshot.children) {
            val target = object: SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Blurry.with(this@ProfileActivity).radius(20).from(resource).into(blurPic)
                    profilePic.setImageBitmap(getCroppedBitmap(resource))
                }
            }
            mStorage!!.child("images/$target_username.gif")
                    .downloadUrl
                    .addOnSuccessListener { uri ->
                        run {
                            Glide.with(this)
                                    .asBitmap()
                                    .load(uri)
                                    .into(target)
                        }
                    }
            preferedName.text = child.child("prefer").value as String
        }
        progressBar.visibility = View.GONE
//                        user_obj = snapshot!!.value as Object
    }

    fun finishWithStatus(reason: String) {
        finishWithStatus(false, reason)
    }
    fun finishWithStatus(success: Boolean=true, reason: String?=null) {
        assert(success && reason == null)
        val result = Intent()
        result.putExtra("success", success)
        if (!success)
            result.putExtra("reason", reason)
        setResult(REQUEST_CODE, result)
        finish()
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

    fun onGenderSelected(view: View) {
        val checked = (view as RadioButton).isChecked
        when (view.id) {
//            R.id.male ->
        }
    }
}
