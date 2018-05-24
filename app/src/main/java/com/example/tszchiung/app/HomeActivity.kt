package com.example.tszchiung.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.example.tszchiung.app.dialog.MatchDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val PROFILE_REQUEST_CODE = 1
    private val SETTINGS_REQUEST_CODE = 2

    interface MatchDialogListener {
        fun getUri(): List<Uri>
        fun fragmentTransaction()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

//        mDrawerLayout.bringToFront()

        drawer_toggle.setOnClickListener { drawer_layout.openDrawer(GravityCompat.START) }
        nav_view.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
//            menuItem.isChecked = true
            when (menuItem.itemId) {
                R.id.home -> startReplaceTransaction(HomeFragment.newInstance())
                R.id.profile ->
                    run {
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivityForResult(intent, PROFILE_REQUEST_CODE)
                    }
                R.id.message -> startReplaceTransaction(MessageFragment.newInstance())
//                R.id.community -> startReplaceTransaction(CommunityFragment.newInstance("Peter", "wahaha"))
                R.id.log_out -> {
                    FirebaseAuth.getInstance()!!.signOut()
                    finish()
                }
            }
            // close drawer when item is tapped
            drawer_layout.closeDrawers()

            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here
            true
        }

        FirebaseAuth.getInstance()!!.addAuthStateListener {
            if (it.currentUser != null && it.currentUser!!.uid.isNotEmpty()) {
                FirebaseDatabase.getInstance()!!.reference
                        .child("users").child(it.currentUser!!.uid)
                        .addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onCancelled(error: DatabaseError?) {
                                finish()
                            }
                            override fun onDataChange(snapshot: DataSnapshot?) {
                                val navHeader = nav_view.getHeaderView(0)
                                navHeader.findViewById<TextView>(R.id.display_name).text =
                                        snapshot!!.child("prefer").value!! as String
                                navHeader.findViewById<TextView>(R.id.year_of_study).text =
                                        "${snapshot!!.child("major").value!!} Year ${snapshot!!.child("year").value!!}"
                            }
                        })
            } else {
                finish()
            }
        }

        startReplaceTransaction(HomeFragment.newInstance())
    }

    fun showMatchDialog(picUri: List<Uri>) {
        val dialog = MatchDialog(this,  object: MatchDialogListener {
            override fun getUri(): List<Uri> {
                return picUri
            }
            override fun fragmentTransaction() {
                TODO("Message fragment")
//                startReplaceTransaction()
            }
        })
//        val lp = dialog.window.attributes
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.show()
//        dialog.window.attributes = lp
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PROFILE_REQUEST_CODE -> {
                if (resultCode == 1 && data != null) {
                    Toast.makeText(this, data.getStringExtra("reason"), Toast.LENGTH_LONG).show()
                }
                // may update database here or update in profile activity
            }
            SETTINGS_REQUEST_CODE -> {}
        }
    }

    private fun startReplaceTransaction(fragment: Fragment, tagInBackStack: String="") {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.placeholder, fragment)
        if (tagInBackStack != "")
            ft.addToBackStack(tagInBackStack)
        ft.commit()
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

//    override fun onPostCreate(savedInstanceState: Bundle?) {
//        super.onPostCreate(savedInstanceState)
//        mDrawerToggle.syncState()
//    }


//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        mDrawerToggle.onConfigurationChanged(newConfig)
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return if (mDrawerToggle.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)
//    }
}