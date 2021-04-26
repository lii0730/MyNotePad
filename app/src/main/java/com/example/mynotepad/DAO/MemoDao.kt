package com.example.mynotepad.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mynotepad.Model.Memo

@Dao
interface MemoDao {

    @Query("SELECT * FROM MEMO")
    fun getAll() : List<Memo>

    @Insert
    fun insertMemo(newMemo : Memo)

    @Query("DELETE FROM Memo")
    fun deleteAll()

    @Update
    fun updateMemoList(memoList : List<Memo>)
}