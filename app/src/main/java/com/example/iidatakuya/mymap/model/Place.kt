package com.example.iidatakuya.mymap.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*


open class Place : RealmObject() {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    @Required
    var name: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var description: String = ""
    var createdAt: Date = Date()
}