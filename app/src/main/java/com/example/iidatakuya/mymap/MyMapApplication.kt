package com.example.iidatakuya.mymap

import android.app.Application
import com.example.iidatakuya.mymap.model.Place
import io.realm.Realm
import io.realm.RealmConfiguration

class MyMapApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val realmConfiguration = RealmConfiguration.Builder().build()
        Realm.deleteRealm(realmConfiguration) // Delete Realm between app restarts.
        Realm.setDefaultConfiguration(realmConfiguration)

        // ダミーデータ
        val realm = Realm.getDefaultInstance()
        var place = Place()
        place.name = "hoge"
        var place2 = Place()
        place2.name = "fuga"
        realm.executeTransaction {
            realm.insert(place)
            realm.insert(place2)
        }

    }
}