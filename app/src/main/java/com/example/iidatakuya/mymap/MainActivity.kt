package com.example.iidatakuya.mymap

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.example.iidatakuya.mymap.fragment.LocationFragment
import com.example.iidatakuya.mymap.fragment.LocationFragment.OnListFragmentInteractionListener
import com.example.iidatakuya.mymap.model.Place
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnListFragmentInteractionListener {

    private lateinit var mRealm: Realm

    override fun onListFragmentInteraction(item: Place?) {
        // TODO: タップ時のインタラクションを設定
        //データベースのオープン処理
        mRealm = Realm.getDefaultInstance()
        // アイテムに紐づく保存場所を取ってくる
        val selectPlace = mRealm.where<Place>(Place::class.java!!)
                .equalTo("id", item?.id)
                .findFirst()

        AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(selectPlace?.name)
                .setMessage(selectPlace?.description)
                .setPositiveButton("マップに移動する") { dialog, whichButton ->

                    val fragment = MapFragment()

                    // データを渡す為の処理　Bundleを生成し、渡すデータを内包させる
                    val bundle = Bundle()
                    bundle.putDouble("longitude", selectPlace!!.longitude) // 引数はkey valueの形
                    bundle.putDouble("latitude", selectPlace!!.latitude)
                    fragment.setArguments(bundle)

                    val transaction = fragmentManager.beginTransaction()
                    // replaceでマップの切り替え　セットされているFragmentを全てRemoveしてから、指定のFragmentをAddする。
                    transaction.replace(R.id.fragments, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                .setNegativeButton("戻る", DialogInterface.OnClickListener { dialog, whichButton -> })
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
