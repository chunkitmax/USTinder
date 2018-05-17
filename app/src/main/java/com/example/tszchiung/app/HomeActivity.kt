package com.example.tszchiung.app

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.Toast

class HomeActivity : AppCompatActivity() {

    private val PROFILE_REQUEST_CODE = 1
    private val SETTINGS_REQUEST_CODE = 2

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mDrawerToggle: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(findViewById(R.id.toolbar))

        mDrawerLayout = findViewById(R.id.drawer_layout)
        mDrawerToggle = findViewById(R.id.drawer_toggle)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
//        mDrawerLayout.bringToFront()

        mDrawerToggle.setOnClickListener { mDrawerLayout.openDrawer(GravityCompat.START) }
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
//            menuItem.isChecked = true
            when (menuItem.itemId) {
                R.id.home -> startReplaceTransaction(HomeFragment.newInstance(false))
                R.id.profile ->
                    run {
                        val intent = Intent(this, ProfileActivity::class.java)
                        intent.putExtra("username", "jackngtszchiu")
                        startActivityForResult(intent, PROFILE_REQUEST_CODE)
                    }
                R.id.recommendation -> startReplaceTransaction(RecommandationFragment.newInstance("Peter", "wahaha"))
            }
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            true
        }

        startReplaceTransaction(HomeFragment.newInstance(false))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PROFILE_REQUEST_CODE -> {
                if (data != null && !data.getBooleanExtra("success", false)) {
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