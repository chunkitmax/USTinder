package com.example.tszchiung.app

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.widget.ImageButton

class HomeActivity : AppCompatActivity() {

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
                R.id.profile -> startReplaceTransaction(ProfileFragment.newInstance("Peter", "wahaha"))
            }
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            true
        }

        startReplaceTransaction(HomeFragment.newInstance(false))
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