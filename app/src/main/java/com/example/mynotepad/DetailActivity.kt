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

        if(CheckIntentForUpdate()) {
            updateDisplayTextView()
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
                if (titleText.text.isNotEmpty() && detailText.text.isNotEmpty()) {

                    if(CheckIntentForUpdate()) {
                        //TODO: 수정 후 저장 작업
                        val tmpMemo = createTmpMemo()
                        memoDatabase.memoDao().updateMemo(titleText.text.toString(), detailText.text.toString(), tmpMemo.id)
                    } else {
                        //TODO: 신규 메모 저장 작업
                        saveToDB(titleText.text.toString(), detailText.text.toString())
                    }

                } else {
                    Toast.makeText(this, "입력한 내용이 없어 메모를 저장하지 않았어요", Toast.LENGTH_SHORT).show()
                }
                this.finish()
                return true
            }

            R.id.addImageAction -> {
                //TODO: 사진 추가

                return true
            }

            R.id.deleteAction -> {
                //TODO: 메모 삭제
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

    private fun updateDisplayTextView() {
        val tmpData = intent.getStringArrayListExtra("memoData")
        this.titleText.setText(tmpData?.get(0))
        this.detailText.setText(tmpData?.get(1))
    }

    private fun CheckIntentForUpdate() : Boolean {
        var rval = false

        if (intent.getStringArrayListExtra("memoData") != null) {
            rval = true
        }

        return rval
    }

    private fun createTmpMemo() : Memo{
        lateinit var needUpdateMemo : Memo
        if(!intent.getStringArrayListExtra("memoData").isNullOrEmpty()) {
            val tmpArray = intent.getStringArrayListExtra("memoData") as ArrayList<String>
            val title = tmpArray[0]
            val text = tmpArray[1]
            val id = tmpArray[2].toInt()
            needUpdateMemo = Memo(id, title, text)
        }
        return needUpdateMemo
    }


}