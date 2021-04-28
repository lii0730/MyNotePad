package com.example.mynotepad

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotepad.Adapters.TrashRecyclerViewAdapter
import com.example.mynotepad.MainActivity.Companion.trashDB
import com.example.mynotepad.Model.DeleteMemo

class TrashListActivity : AppCompatActivity() {

    private val trashRecyclerView : RecyclerView by lazy {
        findViewById(R.id.trashRecyclerView)
    }

    private var trashMemoList : List<DeleteMemo> = emptyList()
    private var trashAdapter : TrashRecyclerViewAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash_list)
        loadTrashMemoList()
        setAdapter()
    }

    fun onDeleteAllButtonClicked(view : View) {
        //TODO: 메모 전체 삭제 기능
        when(view.id) {
            R.id.deleteAllButton -> {
                Thread(Runnable {
                    trashDB.trashDao().deleteAll()
                }).start()
                val intent = Intent(this, this::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
            }
        }
    }

    private fun loadTrashMemoList() {
        val t =Thread(Runnable {
            trashMemoList = trashDB.trashDao().getAll()
        })
        t.start()
        t.join()
    }

    private fun setAdapter() {
        if(trashAdapter == null){
            trashAdapter = TrashRecyclerViewAdapter(trashMemoList)
            trashRecyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        } else {
            trashAdapter?.updateMemoList(trashMemoList)
        }
        trashRecyclerView.adapter = trashAdapter
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        loadTrashMemoList()
        setAdapter()
    }

}