package com.ijpkaushik.bookhub.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.ijpkaushik.bookhub.*
import com.ijpkaushik.bookhub.fragments.AboutAppFragment
import com.ijpkaushik.bookhub.fragments.DashboardFragment
import com.ijpkaushik.bookhub.fragments.FavouritesFragment
import com.ijpkaushik.bookhub.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationLayout: NavigationView

       override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationLayout = findViewById(R.id.navigationView)

        var previousMenuItem: MenuItem? = null

        setUpToolbar()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        openDashboard()

        navigationLayout.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.dashboard -> {
                    openDashboard()
                    drawerLayout.closeDrawers()
                }
                R.id.profile -> {
                    openProfile()
                    drawerLayout.closeDrawers()
                }
                R.id.favourites -> {
                    openFavourites()
                    drawerLayout.closeDrawers()
                }
                R.id.aboutApp -> {
                    openAboutApp()
                    drawerLayout.closeDrawers()
                }
            }
            return@setNavigationItemSelectedListener true

        }
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    fun openDashboard() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, DashboardFragment())
            .commit()

        supportActionBar?.title = "Dashboard"

        navigationLayout.setCheckedItem(R.id.dashboard)

    }

    fun openProfile() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, ProfileFragment())
            .commit()

        supportActionBar?.title = "Profile"

    }

    fun openFavourites() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, FavouritesFragment())
            .commit()

        supportActionBar?.title = "Favourites"

    }

    fun openAboutApp() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, AboutAppFragment())
            .commit()

        supportActionBar?.title = "About App"

    }

    override fun onBackPressed() {

        when (supportFragmentManager.findFragmentById(R.id.frame)) {
            !is DashboardFragment -> openDashboard()

            else -> super.onBackPressed()
        }
    }
}