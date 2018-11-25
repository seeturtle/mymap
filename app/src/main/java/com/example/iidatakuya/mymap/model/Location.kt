package com.example.iidatakuya.mymap.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*


class Location : RealmObject() {
    @PrimaryKey
    private val id: String = UUID.randomUUID().toString()
    private val name: String? = null
    private val latitude: Double = 0.0
    private val longitude: Double = 0.0
    private val discription: String? = null
    private val createdAt: Date? = null
}