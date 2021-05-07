package com.example.mynotepad.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Memo(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo var title : String?,
    @ColumnInfo var text : String?
)
