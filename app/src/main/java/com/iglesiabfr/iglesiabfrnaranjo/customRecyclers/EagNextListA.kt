package com.iglesiabfr.iglesiabfrnaranjo.customRecyclers

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.customRecyclers.items.EagItemA

class EagNextListA(private val dataList: List<EagItemA>,private val img : Drawable) : Adapter<EagNextListA.ViewHolderClass>() {

    var onItemClick : ((EagItemA) -> Unit)? = null

    class ViewHolderClass(itemView: View) : ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.name)
        val date : TextView = itemView.findViewById(R.id.date)
        val bgImg : ImageView = itemView.findViewById(R.id.bgImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.eag_next_list_item,parent,false)
        val viewHolder = ViewHolderClass(itemView)
        viewHolder.bgImg.setImageDrawable(img)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.name.text = currentItem.name
        holder.date.text = currentItem.date
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }
    }
}