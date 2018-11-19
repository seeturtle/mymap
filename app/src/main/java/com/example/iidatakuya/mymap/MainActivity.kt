package com.example.iidatakuya.mymap

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fragmentManager: FragmentManager

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val fragment = MapFragment()
                val transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.fragments, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                val fragment = ListFragment()
                val transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.fragments, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                println("未定のタブ")
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // val mapFragment = supportFragmentManager
        //        .findFragmentById(R.id.map) as SupportMapFragment
        // mapFragment.getMapAsync(this)
        fragmentManager = supportFragmentManager

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // test
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}
