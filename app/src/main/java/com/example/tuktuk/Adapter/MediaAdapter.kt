package com.example.tuktuk.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tuktuk.MediaObject
import com.example.tuktuk.R
import kotlinx.android.synthetic.main.media_object.view.*

class MediaAdapter(private val mediaList: List<MediaObject>) :  RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {



    class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val user_name: TextView = itemView.name
        val date: TextView = itemView.date
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.media_object, parent, false)

        return  MediaViewHolder(itemView)
    }

    override fun getItemCount() = mediaList.size

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val currentObject = mediaList[position]

        holder.user_name.text = currentObject.user_name
        holder.date.text = currentObject.date

    }

}