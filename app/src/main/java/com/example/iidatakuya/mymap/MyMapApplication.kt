package com.example.iidatakuya.mymap

import android.app.Application
import io.realm.Realm

class MyMapApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

    }
}