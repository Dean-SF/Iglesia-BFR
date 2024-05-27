package com.iglesiabfr.iglesiabfrnaranjo.admin.adminLibraryInventory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.schema.LibraryInventory

class LibraryInventoryAdapter(
    private val onClickListener: ((LibraryInventory) -> Unit)?,
    private val onClickDelete:(Int) -> Unit
): RecyclerView.Adapter<LibraryInventoryViewHolder>() {

    private val inventaryLibraryList = mutableListOf<LibraryInventory>()

    val currentList: List<LibraryInventory>
        get() = inventaryLibraryList

    fun submitList(newList: List<LibraryInventory>) {
        inventaryLibraryList.clear()
        inventaryLibraryList.addAll(newList)
        notifyDataSetChanged()
        //notifyItemInserted()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryInventoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LibraryInventoryViewHolder(layoutInflater.inflate(R.layout.item_inventary_library_list, parent, false))
    }

    override fun onBindViewHolder(holder: LibraryInventoryViewHolder, position: Int) {
        val item = inventaryLibraryList[position]
        holder.render(item, onClickListener, onClickDelete)
    }

    override fun getItemCount(): Int = inventaryLibraryList.size
}

