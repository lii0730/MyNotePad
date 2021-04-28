package com.example.mynotepad

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.SHOW_AS_ACTION_ALWAYS
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.mynotepad.MainActivity.Companion.memoDatabase
import com.example.mynotepad.MainActivity.Companion.trashDB
import com.example.mynotepad.Model.DeleteMemo
import com.example.mynotepad.Model.Memo

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

    private lateinit var tmpMemo : Memo
    private lateinit var tmpDeleteMemo: DeleteMemo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(detailActionBar)
        supportActionBar?.title = ""

        if(CheckIntent()) {
            updateDisplayTextView()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_actionbar_actions, menu)
        if(CheckIntent()) {
            val menuItem = menu?.add("삭제 하기")
            menuItem?.setOnMenuItemClickListener {
                //TODO: 삭제 작업
                if(CheckIntent()) {
                    tmpMemo = createTmpMemo()
                    tmpDeleteMemo = createTmpDeleteMemo()
                    deleteFromDB(tmpMemo.id)
                    insertToTrashDB(tmpDeleteMemo)
                    this.finish()
                }
                true
            }
            menuItem?.setIcon(R.drawable.ic_trash)
            menuItem?.setShowAsAction(SHOW_AS_ACTION_ALWAYS)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.saveAction -> {
                //TODO: 메모 DB 저장 기능
                if (titleText.text.isNotEmpty() && detailText.text.isNotEmpty()) {

                    if(CheckIntent()) {
                        //TODO: 수정 후 저장 작업
                        tmpMemo = createTmpMemo()
                        Thread(Runnable {
                            memoDatabase.memoDao().updateMemo(titleText.text.toString(), detailText.text.toString(), tmpMemo.id)
                        }).start()
                    } else {
                        //TODO: 신규 메모 저장 작업
                        saveToDB(titleText.text.toString(), detailText.text.toString())
                    }

                } else {
                    //TODO: 아무런 내용도 입력하지 않은 경우
                    Toast.makeText(this, "입력한 내용이 없어 메모를 저장하지 않았어요", Toast.LENGTH_SHORT).show()
                }
                this.finish()
                return true
            }

            R.id.addImageAction -> {
                //TODO: 사진 추가
                Toast.makeText(this,"사진 추가", Toast.LENGTH_SHORT).show()
                return true
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun deleteFromDB(id : Int?) {
        //TODO: memoDB에서 삭제
        Thread(Runnable {
            memoDatabase.memoDao().deleteMemo(id = id)
        }).start()
    }

    private fun saveToDB(title: String, text: String) {
        //TODO: 신규 메모 memoDB에 저장
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

    private fun insertToTrashDB(memo: DeleteMemo) {
        //TODO: 삭제 메모 휴지통에 추가
        Thread(Runnable {
            trashDB.trashDao().insertMemo(memo)
        }).start()
    }

    private fun updateDisplayTextView() {
        val tmpData = intent.getStringArrayListExtra("memoData")
        this.titleText.setText(tmpData?.get(0))
        this.detailText.setText(tmpData?.get(1))
    }

    private fun CheckIntent() : Boolean {
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

    private fun createTmpDeleteMemo() : DeleteMemo{
        lateinit var needDeleteMemo : DeleteMemo
        if(!intent.getStringArrayListExtra("memoData").isNullOrEmpty()) {
            val tmpArray = intent.getStringArrayListExtra("memoData") as ArrayList<String>
            val title = tmpArray[0]
            val text = tmpArray[1]
            val id = tmpArray[2].toInt()
            needDeleteMemo = DeleteMemo(id, title, text)
        }
        return needDeleteMemo
    }
}