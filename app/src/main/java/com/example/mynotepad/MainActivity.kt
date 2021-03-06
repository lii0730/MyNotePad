package com.example.mynotepad

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
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
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.Buffer
import java.nio.charset.Charset


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
    private  var stringList : ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(actionBar)

        stringList = ArrayList<String>()

        bindButton()
        createDatabase()
        loadMemoList()
        setAdapter()
    }

    override fun onRestart() {
        super.onRestart()
        loadMemoList()
        setAdapter()
        if(drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
    }

    //TODO: actionbar menu action ??????
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.main_actionbar_actions, menu)
//        return true
//    }

    //TODO: menu?????? ?????????(??????) ?????? ?????????
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.selectAction -> {
                Toast.makeText(this, "?????? ?????? ??????!", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun bindButton() {
        //TODO: ?????? ?????? ??????(FAB) ??????
        addNoteButton.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
        }

        //TODO: Drawer Layout Toggle
        mDrawerToggle =
            ActionBarDrawerToggle(this, drawer_layout, actionBar, R.string.open, R.string.close)
    }

    fun onDeleteButtonClicked(view: View) {
        //TODO: Drawer Layout??? ????????? ?????? ??????
        if (view.id == R.id.deleteButton) {
            try {
                val intent = Intent(this@MainActivity, TrashListActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.i("deleteButton", e.toString())
            }
        }
    }

    fun onOpenMemoClicked(view: View) {
        //TODO: ????????????????????? ????????? ?????? ?????? ??? DetailActivity??? Data ??????
        if(view.id == R.id.openNoteButton){

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                val uri : Uri = Uri.parse("content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fdata%2Fcom.example.mynotepad%files")
                setData(uri)
                setType("*/*")
                putExtra("android.provider.extra.INITIAL_URI", uri)
                putExtra("android.content.extra.SHOW_ADVANCED", false)
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(intent, REQUEST_GET_TEXTFILE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_GET_TEXTFILE){
                data?.data.let {
                    contentResolver.query(it!!, null,null,null,null)
                }.use { cursor ->
                    cursor?.moveToFirst()
                    val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val fileName = cursor!!.getString(nameIndex!!)

                    val tmp = File(getExternalFilesDir(null)!!.path + File.separator + fileName)
                    if(tmp.exists()){
                        val fis = FileInputStream(tmp)
                        val reader : InputStreamReader = InputStreamReader(fis, Charset.defaultCharset())
                        val bufReader = BufferedReader(reader)

                        val stringText = bufReader.readText()
                        var id : Int? = null
                        memoList.forEach {
                            if(tmp.nameWithoutExtension.equals(it.title) && stringText.equals(it.text)){
                                id = it.id
                            }
                        }
                        stringList?.add(0, id.toString())
                        stringList?.add(1, tmp.nameWithoutExtension)
                        stringList?.add(2, stringText)
                        val intent = Intent(this, DetailActivity::class.java)
                        intent.putStringArrayListExtra("memoData", stringList)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun createDatabase() {
        //TODO: Memo ?????? DB ??????
        memoDatabase = Room.databaseBuilder(
            applicationContext,
            appDatabase::class.java,
            "memoDB"
        ).build()

        //TODO: ????????? DB ??????
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
        const val REQUEST_GET_TEXTFILE = 100
    }
}