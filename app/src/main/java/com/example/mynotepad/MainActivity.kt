package com.example.mynotepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.LayoutDirection
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.mynotepad.Model.Memo
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private var mDrawerToggle: ActionBarDrawerToggle? = null

    private val drawer_layout: DrawerLayout by lazy {
        findViewById(R.id.drawer_layout)
    }

    private val noteRecyclerView: RecyclerView by lazy {
        findViewById(R.id.noteRecyclerView)
    }

    private val addNoteButton: FloatingActionButton by lazy {
        findViewById(R.id.addNoteButton)
    }

    private val actionBar: Toolbar by lazy {
        findViewById(R.id.actionBar)
    }

    private val deleteButton: Button by lazy {
        findViewById(R.id.deleteButton)
    }

    private var memoList : List<Memo> = emptyList()

    private var adapter : NoteRecyclerViewAdapter? = null


    companion object {
        lateinit var memoDatabase: appDatabase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(actionBar)


        bindButton()
        createDatabase()

        loadMemoList()
        setAdapter()
    }

    override fun onResume() {
        super.onResume()

        setAdapter()
        adapter?.notifyDataSetChanged()
    }

    //TODO: actionbar menu action 생성
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_actionbar_actions, menu)
        return true
    }

    //TODO: menu에서 아이템(액션) 선택 클릭시
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.selectAction -> {
                Toast.makeText(this, "선택 버튼 클릭!", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun bindButton() {
        //TODO: 메모 추가 버튼(FAB) 클릭
        addNoteButton.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
        }

        //TODO: Drawer Layout에 휴지통 버튼 클릭
        deleteButton.setOnClickListener {
            Toast.makeText(this, "휴지통 버튼 클릭!", Toast.LENGTH_SHORT).show()
        }

        //TODO: Drawer Layout Toggle
        mDrawerToggle =
            ActionBarDrawerToggle(this, drawer_layout, actionBar, R.string.open, R.string.close)
        mDrawerToggle!!.syncState()
    }

    private fun createDatabase() {
        memoDatabase = Room.databaseBuilder(
            applicationContext,
            appDatabase::class.java,
            "memoDB"
        ).build()
    }

    private fun loadMemoList(){
        Thread(Runnable {
            memoList = memoDatabase.memoDao().getAll()
            Log.i("memoList", memoList.toString())
        }).start()
    }

    private fun setAdapter() {
//        if(adapter == null) {
//            adapter = NoteRecyclerViewAdapter(memoList)
//            noteRecyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
//            noteRecyclerView.adapter = adapter
//        } else {
//            adapter?.notifyDataSetChanged()
//        }

        if(adapter != null){
            adapter?.notifyDataSetChanged()
        } else {
            adapter = NoteRecyclerViewAdapter(this, memoList)
            noteRecyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
            noteRecyclerView.adapter = adapter
        }

    }
}