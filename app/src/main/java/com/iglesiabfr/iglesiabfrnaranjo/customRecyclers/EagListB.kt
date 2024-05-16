package com.iglesiabfr.iglesiabfrnaranjo.customRecyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.customRecyclers.items.EagItemB

class EagListB(private val dataList: List<EagItemB>) : Adapter<EagListB.ViewHolderClass>() {

    var onItemClick : ((EagItemB) -> Unit)? = null

    class ViewHolderClass(itemView: View) : ViewHolder(itemView) {
        val itemName : TextView = itemView.findViewById(R.id.item_name)
        val itemDate : TextView = itemView.findViewById(R.id.item_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.eag_list_item,parent,false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.itemName.text = currentItem.name
        holder.itemDate.text = currentItem.date
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }
    }
}