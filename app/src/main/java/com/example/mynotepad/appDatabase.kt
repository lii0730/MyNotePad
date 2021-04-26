package com.example.mynotepad

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mynotepad.DAO.MemoDao
import com.example.mynotepad.Model.Memo

@Database(entities = arrayOf(Memo::class), version = 1)
abstract class appDatabase : RoomDatabase(){
    abstract fun memoDao() : MemoDao
}