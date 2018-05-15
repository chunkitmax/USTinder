package com.example.tszchiung.app

import android.app.ActionBar
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.Gravity
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import java.lang.reflect.AccessibleObject.setAccessible

class HomeActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mDrawerToggle: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
//        val actionBar = supportActionBar
//        actionBar!!.setDisplayHomeAsUpEnabled(false)
//        actionBar!!.setDisplayShowTitleEnabled(false)
//        actionBar.setCustomView(R.layout.home_custom_menu)
//        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
//        actionBar.setDisplayShowCustomEnabled(true)
        setSupportActionBar(findViewById(R.id.toolbar))

        mDrawerLayout = findViewById(R.id.drawer_layout)
        mDrawerToggle = findViewById(R.id.drawer_toggle)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
//        mDrawerLayout.bringToFront()

        mDrawerToggle.setOnClickListener { mDrawerLayout.openDrawer(GravityCompat.START) }
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            true
        }
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