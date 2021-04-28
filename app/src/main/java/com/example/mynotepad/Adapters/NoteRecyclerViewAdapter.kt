package com.example.mynotepad.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotepad.DetailActivity
import com.example.mynotepad.Model.Memo
import com.example.mynotepad.R

class NoteRecyclerViewAdapter(val activity: AppCompatActivity ,var memoList: List<Memo>) : RecyclerView.Adapter<NoteRecyclerViewAdapter.ViewHolder>() {

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

//        val item_saveTime : TextView by lazy {
//            itemView.findViewById(R.id.item_saveTime)
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item_title.text = memoList.get(position).title
        holder.item_text.text = memoList.get(position).text
        val memoId = memoList.get(position).id

//        val format = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
//        val currTime = format.format(Calendar.getInstance().time)
//        holder.item_saveTime.text = "$currTime"

        holder.item.setOnClickListener {
            //TODO: 항목을 클릭했을 때 세부 내용으로 넘어감
            val stringArray = arrayListOf<String>(holder.item_title.text.toString(), holder.item_text.text.toString(), memoId.toString())
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putStringArrayListExtra("memoData", stringArray)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return memoList.size
    }

    fun updateMemoList(newMemoList: List<Memo>) {
        this.memoList = newMemoList
    }
}