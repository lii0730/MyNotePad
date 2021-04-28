package com.example.mynotepad.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotepad.Model.DeleteMemo
import com.example.mynotepad.Model.Memo
import com.example.mynotepad.R

class TrashRecyclerViewAdapter(var trashList : List<DeleteMemo>) : RecyclerView.Adapter<TrashRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val trashMemoTitle : TextView by lazy {
            itemView.findViewById(R.id.trashMemoTitle)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trash_recyclerview_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.trashMemoTitle.text = trashList.get(position).title
    }

    override fun getItemCount(): Int {
        return trashList.size
    }

    fun updateMemoList(newMemoList: List<DeleteMemo>) {
        this.trashList = newMemoList
    }
}