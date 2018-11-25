package com.example.iidatakuya.mymap.fragment

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.iidatakuya.mymap.R
import com.example.iidatakuya.mymap.model.Place
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

internal class MyLocationRecyclerViewAdapter(data: OrderedRealmCollection<Place>) :
        RealmRecyclerViewAdapter<Place, MyLocationRecyclerViewAdapter.MyViewHolder>(data, true) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_location, parent, false)
        return MyViewHolder(itemView)
    }

    // 実際にアイテムに受け取ったデータをセット
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val obj = getItem(position)

        //ビューホルダーに値を入れる
        holder.name?.text = obj?.name
    }

//    override fun getItemId(index: Int): Long {
//        return getItem(index)!!.id.toLong()
//    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView? = null

        init {
            //ビューホルダーの情報がレイアウトのどれと対応するか
            name = view.findViewById(R.id.content)
        }
    }
}