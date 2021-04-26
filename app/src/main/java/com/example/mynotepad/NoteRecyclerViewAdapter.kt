package com.example.mynotepad

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotepad.Model.Memo

class NoteRecyclerViewAdapter(val activity: AppCompatActivity ,val memoList: List<Memo>) : RecyclerView.Adapter<NoteRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : View) :RecyclerView.ViewHolder(itemView) {
        val item : LinearLayout by lazy {
            itemView.findViewById(R.id.item)
        }

        val item_title : TextView by lazy {
            itemView.findViewById(R.id.item_title)
        }

        val item_text : TextView by lazy {
            itemView.findViewById(R.id.item_text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item_title.text = memoList.get(position).title
        holder.item_text.text = memoList.get(position).text

        holder.item.setOnClickListener {
            //TODO: 항목을 클릭했을 때 세부 내용으로 넘어감
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("title", holder.item_title.text)
            intent.putExtra("text", holder.item_text.text)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return memoList.size
    }
}