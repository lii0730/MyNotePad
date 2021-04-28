package com.example.mynotepad.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mynotepad.DAO.TrashDao
import com.example.mynotepad.Model.DeleteMemo

@Database(entities = arrayOf(DeleteMemo::class), version = 1)
abstract class trashDatabase : RoomDatabase(){
    abstract fun trashDao() : TrashDao
}