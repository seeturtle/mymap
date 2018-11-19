package com.example.iidatakuya.mymap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import kotlinx.android.synthetic.main.activity_main.*




class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    // 初期位置を六甲山に
    private val mLatitude = 34.0 + 46.0 / 60 + 41.0 / (60 * 60)
    private val mLongitude = 135.0 + 15.0 / 60 + 49.0 / (60 * 60)

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
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fragmentManager = supportFragmentManager

        val fragment = MapFragment()
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragments, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener

        // 位置情報パーミッションの通知
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1000)
        }
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

        // 長押し処理リスナー
        mMap.setOnMapLongClickListener(this)


        val location = LatLng(mLatitude, mLongitude)
        val cameraPos = CameraPosition.Builder()
                .target(location).zoom(13.0f)
                .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos))

        // MyLocationレイヤーを有効に
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 位置情報許可しなかった場合はMyLocation表示までいかずreturn
            return
        }
        mMap.isMyLocationEnabled = true

        // MyLocationButtonを有効に
        val settings = mMap.uiSettings
        settings.isMyLocationButtonEnabled = true
    }

    //  長押し検知
    override fun onMapLongClick(p0: LatLng?) {
        // ピンを立てる
        mMap.addMarker(MarkerOptions().position(p0!!).title("LongClick").draggable(false))
    }

}
