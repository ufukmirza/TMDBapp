package com.example.tmdbapp

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.tmdbapp.ui.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() ,HomeFragment.OnHeadlineSelectedListener{

    var navView: BottomNavigationView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        delegate.applyDayNight()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        navView  = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)


var fragment:Fragment?=supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

        if(fragment is HomeFragment){
            Log.d("sa", "hsajkl")

        }

//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.let {
            if (it != null) {
                it.setupWithNavController(navController)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        return item.onNavDestinationSelected(findNavController(R.id.nav_host_fragment)) || super.onOptionsItemSelected(item)
    }



    override fun onAttachFragment(fragment: Fragment) {

        if (fragment is HomeFragment) {
            fragment.setOnHeadlineSelectedListener(this)
        }
    }

    override fun onArticleSelected() {



    }

}
