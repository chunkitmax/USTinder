package com.example.tszchiung.app

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_image.*
import java.io.IOException

class ImageActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "StorageActivity"
    private val CHOOSING_IMAGE_REQUEST = 1234

    private var fileUri: Uri? = null
    private var bitmap: Bitmap? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var imageReference: StorageReference
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        // Layout
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        var lp = bg.layoutParams as ViewGroup.MarginLayoutParams
        lp.setMargins(lp.leftMargin, lp.topMargin-resources.getDimensionPixelSize(resId), lp.rightMargin, lp.bottomMargin)
        bg.layoutParams = lp
        StatusBarUtil.setTranslucentForImageView(this, 70, bg)

        FirebaseAuth.getInstance()!!.addAuthStateListener {
            val user = it.currentUser
            if (user != null) {
                choose.setOnClickListener(this)
                upload.setOnClickListener(this)
                mAuth = it
                mDatabase = FirebaseDatabase.getInstance().reference.child("users").child(user.uid)
                imageReference = FirebaseStorage.getInstance().reference.child("images")
            } else {
                finishWithStatus(false,"Cannot get current user!")
            }
        }
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.choose -> showChoosingFile()
            R.id.upload -> uploadFile()
        }
    }

    private fun uploadFile() {
        if (fileUri != null) {
            mDatabase.child("username")
                    .addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onCancelled(error: DatabaseError?) {
                            finishWithStatus(error!!.message)
                        }
                        override fun onDataChange(snapshot: DataSnapshot?) {
                            try {
                                val fileRef = imageReference.child("${snapshot!!.value as String}.${getFileExtension(fileUri!!)}")
                                progress_bar.progress = 0
                                progress_bar.visibility = View.VISIBLE
                                fileRef.putFile(fileUri!!)
                                        .addOnSuccessListener {
                                            val childUpdates = HashMap<String, Any>()
                                            childUpdates["ext"] = getFileExtension(fileUri!!)
                                            mDatabase.updateChildren(childUpdates)
                                                    .addOnCompleteListener {
                                                        if (it.isSuccessful) {
                                                            finishWithStatus(true)
                                                        } else {
                                                            finishWithStatus(it.exception!!.message!!)
                                                        }
                                                    }
                                        }
                                        .addOnFailureListener { exception ->
                                            Toast.makeText(this@ImageActivity, exception.message, Toast.LENGTH_LONG).show()
                                        }
                                        .addOnProgressListener { taskSnapshot ->
                                            progress_bar.progress = (100.0 * taskSnapshot.bytesTransferred / progress_bar.max).toInt()
                                        }
                                        .addOnPausedListener { System.out.println("Upload is paused!") }
                                intent
                            } catch (e: Exception) {
                                finishWithStatus(e.message!!)
                            }
                        }
                    })

        } else {
            Toast.makeText(this, "No File chosen!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (bitmap != null) {
            bitmap!!.recycle()
        }
        if (requestCode == CHOOSING_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            fileUri = data.data
            try {
                Glide.with(this).load(fileUri).into(preview)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun showChoosingFile() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSING_IMAGE_REQUEST)
    }

    private fun getFileExtension(uri: Uri): String {
        val contentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()

        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
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
