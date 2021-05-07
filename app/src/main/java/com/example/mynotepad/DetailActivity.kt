package com.example.mynotepad

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.SHOW_AS_ACTION_ALWAYS
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mynotepad.MainActivity.Companion.memoDatabase
import com.example.mynotepad.MainActivity.Companion.trashDB
import com.example.mynotepad.Model.DeleteMemo
import com.example.mynotepad.Model.Memo
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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

    private var memoList = mutableListOf<Memo>()

    private lateinit var tmpMemo: Memo
    private lateinit var tmpDeleteMemo: DeleteMemo


    private val PERMISSIONS_REQUEST_CODE = 100
    private val REQUEST_GET_IMAGE = 200
    private var REQUIRED_PERMISSIONS =
        arrayOf<String>(android.Manifest.permission.READ_EXTERNAL_STORAGE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(detailActionBar)
        detailText.isSaveFromParentEnabled = false
        detailText.isSaveEnabled = true

        if (CheckIntent()) {
            updateDisplayTextView()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_actionbar_actions, menu)
        if (CheckIntent()) {
            val menuItem = menu?.add("삭제 하기")
            menuItem?.setIcon(R.drawable.ic_trash)
            menuItem?.setShowAsAction(SHOW_AS_ACTION_ALWAYS)
            menuItem?.setOnMenuItemClickListener {
                //TODO: 삭제 작업
                if (CheckIntent()) {
                    tmpMemo = createTmpMemo()
                    tmpDeleteMemo = createTmpDeleteMemo()
                    deleteMemoFile(tmpMemo)
                    deleteFromDB(tmpMemo.id)
                    insertToTrashDB(tmpDeleteMemo)
                    this.finish()
                }
                true
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.saveAction -> {
                //TODO: 메모 DB 저장 기능
                if (titleText.text.isNotEmpty() || detailText.text.isNotEmpty()) {

                    if (CheckIntent()) {
                        //TODO: 수정 후 저장 작업
                        tmpMemo = createTmpMemo()
                        updateMemoFile(tmpMemo)
                        Thread(Runnable {
                            memoDatabase.memoDao().updateMemo(
                                titleText.text.toString(),
                                detailText.text.toString(),
                                tmpMemo.id
                            )
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
                //TODO: 권한 받기
                checkPermissions()
                return true
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun updateMemoFile(memo: Memo) {
        val t = Thread {
            memoList = memoDatabase.memoDao().getAll() as MutableList<Memo>
        }
        t.start()
        t.join()

        memo.title = titleText.text.toString()
        memo.text = detailText.text.toString()

        memoList.forEachIndexed { index, it ->
            if (it.id == memo.id) {
                memoList.set(index, memo)
                try {
                    var tmp = File(getExternalFilesDir(null)!!.path + File.separator + "${it.title}.txt")
                    if(tmp.exists()) {
                        tmp.delete()
                    }
                    tmp =
                        File(getExternalFilesDir(null)!!.path + File.separator + "${memo.title}.txt")
                    val fos = FileOutputStream(tmp)
                    fos.write(memo.text?.toByteArray())
                    fos.close()
                } catch (e: java.lang.Exception) {
                    Log.i("UpdateError", e.toString())
                }
            }
        }
    }

    private fun deleteMemoFile(memo: Memo) {
        val t = Thread {
            memoList = memoDatabase.memoDao().getAll() as MutableList<Memo>
        }
        t.start()
        t.join()

        memoList.forEach {
            if (it.id == memo.id) {
                try {
                    val file =
                        File(getExternalFilesDir(null)!!.path + File.separator + "${it.title}.txt")
                    if (file.exists()) {
                        file.delete()
                        memoList.remove(it)
                    }
                } catch (e : java.lang.Exception) {
                    Log.i("deleteError", e.toString())
                }
            }
        }
    }

    private fun deleteFromDB(id: Int?) {
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

            //TODO: sdcard에 파일로 저장
            saveToStorage(memo)

        } catch (e: Exception) {
            Log.i("saveDB Exception", e.toString())
        }
    }

    private fun saveToStorage(memo: Memo) {
        try {
            val fileDir =
                File(getExternalFilesDir(null)!!.path + File.separator + "${memo.title}.txt")
            val fos = FileOutputStream(fileDir)
            fos.write(memo.text?.toByteArray())
            fos.close()
            memoList.add(memo)

        } catch (e: java.lang.Exception) {
            Log.i("SaveFileError", e.toString())
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

    private fun CheckIntent(): Boolean {
        var rval = false
        if (intent.getStringArrayListExtra("memoData") != null) {
            rval = true
        }
        return rval
    }

    private fun createTmpMemo(): Memo {
        lateinit var needUpdateMemo: Memo
        if (!intent.getStringArrayListExtra("memoData").isNullOrEmpty()) {
            val tmpArray = intent.getStringArrayListExtra("memoData") as ArrayList<String>
            val title = tmpArray[0]
            val text = tmpArray[1]
            val id = tmpArray[2].toInt()
            needUpdateMemo = Memo(id, title, text)
        }
        return needUpdateMemo
    }

    private fun createTmpDeleteMemo(): DeleteMemo {
        lateinit var needDeleteMemo: DeleteMemo
        if (!intent.getStringArrayListExtra("memoData").isNullOrEmpty()) {
            val tmpArray = intent.getStringArrayListExtra("memoData") as ArrayList<String>
            val title = tmpArray[0]
            val text = tmpArray[1]
            val id = tmpArray[2].toInt()
            needDeleteMemo = DeleteMemo(id, title, text)
        }
        return needDeleteMemo
    }

    private fun checkPermissions() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        //TODO: 권한 부여x
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                showPermissionContextPopUp()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        } else {
            // TODO: 권한 부여
            //  사진 선택 기능
            navigatePhotos()
        }
    }

    private fun showPermissionContextPopUp() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("메모장 앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
            .setNegativeButton("취소하기", null)
            .create()
            .show()
    }

    private fun navigatePhotos() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GET_IMAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigatePhotos()
                } else {
                    Toast.makeText(this, "권한을 거부하였습니다.", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_GET_IMAGE -> {
                    //TODO: EditText에 사진 추가 작업을 해주어야 하는데!
                    Log.i("DetailActivity", data?.data.toString())

                    val builder = SpannableStringBuilder(detailText.text)
                    val inputStream: InputStream = contentResolver.openInputStream(data?.data!!)!!
                    val image: Drawable = Drawable.createFromStream(inputStream, "addImage")
                    image.setBounds(0, 0, 100, 100)

                    detailText.setSelection(detailText.text.length)

                    val imageSpan = ImageSpan(image)
                    val start = builder.getSpanStart(imageSpan)
                    val end = builder.getSpanEnd(imageSpan)
                    try {
                        builder.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        detailText.text = builder
                    } catch (e: Exception) {
                        Log.e("DetailActivity", e.toString())
                    }
                }
            }
        }
    }
}