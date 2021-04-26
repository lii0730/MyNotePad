package com.example.mynotepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.mynotepad.MainActivity.Companion.memoDatabase
import com.example.mynotepad.Model.Memo
import java.lang.Exception

class DetailActivity : AppCompatActivity() {

    private val detailActionBar: Toolbar by lazy {
        findViewById(R.id.detailActionBar)
    }

    private val titleText: EditText by lazy {
        findViewById(R.id.titleText)
    }

    private val detailText: EditText by lazy {
        findViewById(R.id.detailText)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(detailActionBar)
        supportActionBar?.title = ""

        if(intent != null){
            updateTextView(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_actionbar_actions, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.saveAction -> {

                //TODO: 메모 DB 저장 기능
                saveToDB(titleText.text.toString(), detailText.text.toString())

                Toast.makeText(this, "저장 버튼 클릭!", Toast.LENGTH_SHORT).show()
                this.finish()
                return true
            }

            R.id.addImageAction -> {
                //TODO: 사진 추가

                return true
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun saveToDB(title: String, text: String) {
        try {
            val memo = Memo(null, title, text)
            Thread(Runnable {
                memoDatabase.memoDao().insertMemo(
                    memo
                )
            }).start()
        } catch (e: Exception) {
            Log.i("saveDB Exception", e.toString())
        }
    }

    private fun updateTextView(intent: Intent) {
        this.titleText.setText(intent.getStringExtra("title"))
        this.detailText.setText(intent.getStringExtra("text"))
    }

}