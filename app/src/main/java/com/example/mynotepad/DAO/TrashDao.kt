package com.example.mynotepad.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mynotepad.Model.DeleteMemo

@Dao
interface TrashDao{
    @Query("SELECT * FROM DeleteMemo")
    fun getAll() : List<DeleteMemo>

    @Insert
    fun insertMemo(newMemo : DeleteMemo)

    @Query("DELETE FROM DeleteMemo")
    fun deleteAll()
}