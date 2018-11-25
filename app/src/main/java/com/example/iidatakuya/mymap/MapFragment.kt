package com.example.iidatakuya.mymap

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.iidatakuya.mymap.model.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.realm.Realm
import io.realm.kotlin.createObject
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var mRealm: Realm
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

        // マップのView表示
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

        // 初期位置を設定(仮)
        val location = LatLng(mLatitude, mLongitude)
        val cameraPos = CameraPosition.Builder()
                .target(location).zoom(13.0f)
                .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos))


        //データベースのオープン処理
        mRealm = Realm.getDefaultInstance()

        // 保存しているピン表示
        var savedPlace = mRealm.where<Place>(Place::class.java!!).findAll()
        for(fav in savedPlace){
            mMap.addMarker(MarkerOptions().position(LatLng(fav.latitude,fav.longitude)!!).title(fav.name).draggable(false))
        }


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

        var contents : String

        //テキスト入力を受け付けるビューを作成
        val editView = EditText(context)
        AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("場所を追加")
                //setViewにてビューを設定します。
                .setView(editView)
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, whichButton ->
                    //入力した文字を保存
                    contents = editView.text.toString()

                    // ピンを立てる
                    mMap.addMarker(MarkerOptions().position(p0!!).title(contents).draggable(false))

                    // データを保存
                    mRealm.executeTransaction {
                        //新規Place作成
                        val place: Place = mRealm.createObject<Place>(primaryKeyValue = UUID.randomUUID().toString())
                        // データ挿入
                        place.name = contents
                        place.latitude = p0.latitude
                        place.longitude = p0.longitude
                    }
                })
                .setNegativeButton("キャンセル", DialogInterface.OnClickListener { dialog, whichButton -> })
                .show()
    }
}
