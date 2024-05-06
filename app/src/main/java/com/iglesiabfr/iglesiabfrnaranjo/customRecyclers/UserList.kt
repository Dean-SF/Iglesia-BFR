package com.iglesiabfr.iglesiabfrnaranjo.customRecyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.customRecyclers.items.UserItem

class UserList(private val dataList: List<UserItem>) : RecyclerView.Adapter<UserList.ViewHolderClass>() {

    var onItemClick : ((UserItem) -> Unit)? = null

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName : TextView = itemView.findViewById(R.id.item_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_list_item,parent,false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.itemName.text = currentItem.name
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }
    }
}