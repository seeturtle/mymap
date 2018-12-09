package com.example.iidatakuya.mymap

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.iidatakuya.mymap.fragment.LocationFragment
import com.example.iidatakuya.mymap.fragment.LocationFragment.OnListFragmentInteractionListener
import com.example.iidatakuya.mymap.model.Place
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnListFragmentInteractionListener {

    private lateinit var mRealm: Realm

    override fun onListFragmentInteraction(item: Place?) {
        //データベースのオープン処理
        mRealm = Realm.getDefaultInstance()
        // アイテムに紐づく保存場所を取ってくる
        val selectPlace = mRealm.where<Place>(Place::class.java)
                .equalTo("id", item?.id)
                .findFirst()

        // アラートダイアログビルダーのインスタンス生成
        val dialog = AlertDialog.Builder(this)

        // レイアウト作成（外枠とパーツの作成）
        val layout = LinearLayout(this)
        // 上から下にパーツを組み込む設定
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER

        // レイアウトに組み込むパーツの作成
        val description = TextView(this)
        description.text = selectPlace?.description
        val imageView = ImageView(this)
        selectPlace?.image?.let { byteArray ->
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            imageView.setImageBitmap(bitmap)
        }

        //外枠にパーツを組み込む
        layout.addView(description, LinearLayout.LayoutParams(500, 300))
        layout.addView(imageView, LinearLayout.LayoutParams(500, 500))

        dialog.setView(layout)
                .setTitle(selectPlace?.name)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("マップに移動する") { dialog, whichButton ->

                    val fragment = MapFragment()

                    // データを渡す為の処理　Bundleを生成し、渡すデータを内包させる
                    val bundle = Bundle()
                    bundle.putDouble("longitude", selectPlace!!.longitude) // 引数はkey valueの形
                    bundle.putDouble("latitude", selectPlace!!.latitude)
                    fragment.arguments = bundle

                    val transaction = fragmentManager.beginTransaction()
                    // replaceでマップの切り替え　セットされているFragmentを全てRemoveしてから、指定のFragmentをAddする。
                    transaction.replace(R.id.fragments, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                .setNegativeButton("戻る") { dialog, whichButton -> }
                .show()

    }

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
                val fragment = LocationFragment()
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

        fragmentManager = supportFragmentManager
        val fragment = MapFragment()
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragments, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

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

}
