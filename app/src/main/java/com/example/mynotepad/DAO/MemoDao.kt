package com.example.mynotepad.DAO

import androidx.room.*
import com.example.mynotepad.Model.Memo

@Dao
interface MemoDao {

    @Query("SELECT * FROM MEMO")
    fun getAll() : List<Memo>

    @Insert
    fun insertMemo(newMemo : Memo)

    @Query("DELETE FROM Memo")
    fun deleteAll()

    @Query("Update Memo Set title = :newTitle, text = :newText Where id == :id")
    fun updateMemo(newTitle : String?, newText : String?, id:Int?)

    @Delete
    fun deleteMemo(memo : Memo)
}