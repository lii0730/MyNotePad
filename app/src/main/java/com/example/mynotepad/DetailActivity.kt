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
        supportActionBar?.title = ""
        detailText.isSaveFromParentEnabled = false
        detailText.isSaveEnabled = true

        if (CheckIntent()) {
            updateDisplayTextView()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_actionbar_actions, menu)
        if (CheckIntent()) {
            val menuItem = menu?.add("?????? ??????")
            menuItem?.setIcon(R.drawable.ic_trash)
            menuItem?.setShowAsAction(SHOW_AS_ACTION_ALWAYS)

            menuItem?.setOnMenuItemClickListener {
                //TODO: ?????? ??????
                tmpMemo = createTmpMemo()
                tmpDeleteMemo = createTmpDeleteMemo()
                deleteMemoFile(tmpMemo)
                deleteFromDB(tmpMemo.id)
                insertToTrashDB(tmpDeleteMemo)
                this.finish()
                true
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.saveAction -> {
                //TODO: ?????? DB ?????? ??????
                if (titleText.text.isNotEmpty() || detailText.text.isNotEmpty()) {

                    if (CheckIntent()) {
                        //TODO: ?????? ??? ?????? ??????
                            //TODO: ?????? ?????? ?????? ??????????????? ?????? ?????????
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
                        //TODO: ?????? ?????? ?????? ??????
                        saveToDB(titleText.text.toString(), detailText.text.toString())
                    }

                } else {
                    //TODO: ????????? ????????? ???????????? ?????? ??????
                    Toast.makeText(this, "????????? ????????? ?????? ????????? ???????????? ????????????", Toast.LENGTH_SHORT).show()
                }
                this.finish()
                return true
            }

            R.id.addImageAction -> {
                //TODO: ?????? ??????
                //TODO: ?????? ??????
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
        //TODO: memoDB?????? ??????
        Thread(Runnable {
            memoDatabase.memoDao().deleteMemo(id = id)
        }).start()
    }

    private fun saveToDB(title: String, text: String) {
        //TODO: ?????? ?????? memoDB??? ??????
        try {
            val memo = Memo(null, title, text)
            Thread(Runnable {
                memoDatabase.memoDao().insertMemo(
                    memo
                )
            }).start()

            //TODO: sdcard??? ????????? ??????
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
        //TODO: ?????? ?????? ???????????? ??????
        Thread(Runnable {
            trashDB.trashDao().insertMemo(memo)
        }).start()
    }

    private fun CheckIntent(): Boolean {
        var rval = false
        if (intent.getStringArrayListExtra("memoData") != null) {
            rval = true
        }
        return rval
    }


    private fun updateDisplayTextView() {
        val tmpData = intent.getStringArrayListExtra("memoData")
        this.titleText.setText(tmpData?.get(1))
        this.detailText.setText(tmpData?.get(2))
    }

    private fun createTmpMemo(): Memo {
        lateinit var needUpdateMemo: Memo

        val tmpArray = intent.getStringArrayListExtra("memoData") as ArrayList<String>
        val id = tmpArray[0].toInt()
        val title = tmpArray[1]
        val text = tmpArray[2]
        val memo = Memo(id,title,text)
        needUpdateMemo = Memo(memo.id, memo.title, memo.text)

        return needUpdateMemo
    }

    private fun createTmpDeleteMemo(): DeleteMemo {
        lateinit var needDeleteMemo: DeleteMemo

        val tmpArray = intent.getStringArrayListExtra("memoData") as ArrayList<String>
        val id = tmpArray[0].toInt()
        val title = tmpArray[1]
        val text = tmpArray[2]
        val memo = Memo(id, title, text)
        needDeleteMemo = DeleteMemo(memo.id, memo.title, memo.text)

        return needDeleteMemo
    }

    private fun checkPermissions() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        //TODO: ?????? ??????x
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
            // TODO: ?????? ??????
            //  ?????? ?????? ??????
            navigatePhotos()
        }
    }

    private fun showPermissionContextPopUp() {
        AlertDialog.Builder(this)
            .setTitle("????????? ???????????????.")
            .setMessage("????????? ????????? ????????? ???????????? ?????? ????????? ???????????????.")
            .setPositiveButton("????????????") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
            .setNegativeButton("????????????", null)
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
                    Toast.makeText(this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
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
                    //TODO: EditText??? ?????? ?????? ????????? ???????????? ?????????!
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