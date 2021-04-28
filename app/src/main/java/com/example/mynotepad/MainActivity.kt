package com.example.mynotepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.mynotepad.Adapters.NoteRecyclerViewAdapter
import com.example.mynotepad.Database.appDatabase
import com.example.mynotepad.Database.trashDatabase
import com.example.mynotepad.Model.DeleteMemo
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

    private var memoList: List<Memo> = emptyList()
    private var trashList: List<DeleteMemo> = emptyList()

    private var adapter: NoteRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(actionBar)

        bindButton()
        createDatabase()
        loadMemoList()
        setAdapter()
    }

    override fun onRestart() {
        super.onRestart()
        loadMemoList()
        setAdapter()
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
        //TODO: Drawer Layout Toggle
        mDrawerToggle =
            ActionBarDrawerToggle(this, drawer_layout, actionBar, R.string.open, R.string.close)
        mDrawerToggle!!.syncState()
    }

    fun onDeleteButtonClicked(view: View) {
        //TODO: Drawer Layout에 휴지통 버튼 클릭
        if (view.id == R.id.deleteButton) {
            try {
                val intent = Intent(this@MainActivity, TrashListActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.i("deleteButton", e.toString())
            }
        }
    }

    private fun createDatabase() {
        //TODO: Memo 저장 DB 생성
        memoDatabase = Room.databaseBuilder(
            applicationContext,
            appDatabase::class.java,
            "memoDB"
        ).build()

        //TODO: 휴지통 DB 생성
        trashDB = Room.databaseBuilder(
            applicationContext,
            trashDatabase::class.java,
            "memoTrashDB"
        ).build()
    }


    private fun loadMemoList() {
        val t = Thread(Runnable {
            memoList = memoDatabase.memoDao().getAll()
            trashList = trashDB.trashDao().getAll()
            Log.i("onCreate_memoList", memoList.size.toString())
            Log.i("onCreate_trashList", trashList.size.toString())
        })
        t.start()
        t.join()
    }

    private fun setAdapter() {
        if (adapter != null) {
            adapter?.updateMemoList(memoList)
        } else {
            adapter = NoteRecyclerViewAdapter(this, memoList)
            noteRecyclerView.layoutManager =
                GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        }
        noteRecyclerView.adapter = adapter
    }


    companion object {
        lateinit var memoDatabase: appDatabase
        lateinit var trashDB: trashDatabase
    }
}