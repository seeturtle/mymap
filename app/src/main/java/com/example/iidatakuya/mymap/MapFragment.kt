package com.example.iidatakuya.mymap

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.iidatakuya.mymap.model.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.realm.Realm
import io.realm.kotlin.createObject
import java.io.ByteArrayOutputStream
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var mRealm: Realm
    // 初期位置を六甲山に
    private var mLatitude = 34.0 + 46.0 / 60 + 41.0 / (60 * 60)
    private var mLongitude = 135.0 + 15.0 / 60 + 49.0 / (60 * 60)

    val RESULT_PICK_IMAGEFILE = 1000
    private lateinit var imageView: ImageView

    private var uri: Uri? = null

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
        if (ContextCompat.checkSelfPermission(context as Activity,
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
        // マーカークリック処理リスナー
        mMap.setOnMarkerClickListener(this)

        // リストから移動してくる場合はその位置へ飛ぶため座標を設定し直す
        val bundle = arguments
        if (bundle != null) {
            mLatitude = bundle.getDouble("latitude")
            mLongitude = bundle.getDouble("longitude")
        }


        // 初期位置を設定(仮)
        val location = LatLng(mLatitude, mLongitude)
        val cameraPos = CameraPosition.Builder()
                .target(location).zoom(13.0f)
                .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos))


        //データベースのオープン処理
        mRealm = Realm.getDefaultInstance()

        // 保存しているピン表示
        val savedPlace = mRealm.where<Place>(Place::class.java!!).findAll()
        for (fav in savedPlace) {
            mMap.addMarker(MarkerOptions().position(LatLng(fav.latitude, fav.longitude)!!).title(fav.name).draggable(false))
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

    override fun onMarkerClick(p0: Marker?): Boolean {

        val markerPosition  = p0!!.position
        val markerlatitude = markerPosition.latitude
        val markerlongitude = markerPosition.longitude

        val markerPlace = mRealm.where<Place>(Place::class.java!!)
                .equalTo("latitude",markerlatitude)
                .equalTo("longitude",markerlongitude)
                .findFirst()

        // アラートダイアログビルダーのインスタンス生成
        val dialog = AlertDialog.Builder(context)

        // レイアウト作成（外枠とパーツの作成）
        val layout = LinearLayout(context)
        // 上から下にパーツを組み込む設定
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER

        // レイアウトに組み込むパーツの作成

        // 画像
        val imageView = ImageView(context)
        markerPlace?.image?.let { byteArray ->
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            imageView.setImageBitmap(bitmap)
        }

        //外枠にパーツを組み込む
        layout.addView(imageView,LinearLayout.LayoutParams(500, 500))

        //レイアウトをダイアログに設定
        dialog.setView(layout)
        //タイトルの設定
        dialog.setTitle(markerPlace!!.name).setMessage(markerPlace.description)

        // dialogOKボタン
        dialog.setNegativeButton("閉じる") { dialog, whichButton -> }
              .show()

        return false
    }

    //  長押し検知
    override fun onMapLongClick(p0: LatLng?) {

        var name: String
        var description: String

        // アラートダイアログビルダーのインスタンス生成
        val dialog = AlertDialog.Builder(context)

        // レイアウト作成（外枠とパーツの作成）
        val layout = LinearLayout(context)
        // 上から下にパーツを組み込む設定
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER

        // レイアウトに組み込むパーツの作成
        val textView1 = TextView(context)
        textView1.text = "場所の名前"
        val textView2 = TextView(context)
        textView2.text = "詳細情報"
        // テキスト入力を受け付けるパーツ
        val editView1 = EditText(context)
        val editView2 = EditText(context)

        // 画像追加ボタン
        val addImageButton = Button(context)
        addImageButton.text = "画像を追加"
        addImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, RESULT_PICK_IMAGEFILE)
        }

        // 画像
        imageView = ImageView(context)

        //外枠にパーツを組み込む
        layout.addView(textView1, LinearLayout.LayoutParams(500, 80))
        layout.addView(editView1, LinearLayout.LayoutParams(500, 100))
        layout.addView(textView2, LinearLayout.LayoutParams(500, 80))
        layout.addView(editView2, LinearLayout.LayoutParams(500, 300))
        layout.addView(addImageButton, LinearLayout.LayoutParams(300, 120))
        layout.addView(imageView, LinearLayout.LayoutParams(500, 500))

        //レイアウトをダイアログに設定
        dialog.setView(layout)
        //タイトルの設定
        dialog.setTitle("場所を追加")

        // dialogOKボタン
        dialog.setPositiveButton("OK") { dialog, whichButton ->

            //入力した文字を保存
            name = editView1.text.toString()
            description = editView2.text.toString()

            // ピンを立てる
            mMap.addMarker(MarkerOptions().position(p0!!).title(name).draggable(false))

            // データを保存
            mRealm.executeTransaction {
                //新規Place作成
                val place: Place = mRealm.createObject(primaryKeyValue = UUID.randomUUID().toString())
                // データ挿入
                place.name = name
                place.latitude = p0.latitude
                place.longitude = p0.longitude
                place.description = description

                // 画像保存
                imageView.drawable?.let {
                    val bitmap = (it as BitmapDrawable).bitmap
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                    val imageByteArray = baos.toByteArray()
                    place.image = imageByteArray

                    place.imageUri = uri?.toString()
                }
            }
        }
                .setNegativeButton("キャンセル") { dialog, whichButton -> }
                .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == RESULT_OK) {
            if (data != null) {
                uri = data.data
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
                imageView.setImageBitmap(bitmap)
            }
        }
    }
}
