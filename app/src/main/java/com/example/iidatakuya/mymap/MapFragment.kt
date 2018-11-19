package com.example.iidatakuya.mymap

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    // 初期位置を六甲山に
    private val mLatitude = 34.0 + 46.0 / 60 + 41.0 / (60 * 60)
    private val mLongitude = 135.0 + 15.0 / 60 + 49.0 / (60 * 60)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val mapFragment = childFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 位置情報パーミッションの通知
        if(ContextCompat.checkSelfPermission(context as Activity,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1000)
        }
    }

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
        if (ActivityCompat.checkSelfPermission(context as Activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context as Activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
