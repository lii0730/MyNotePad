package com.example.mynotepad.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.PropertyKey


@Entity
data class Memo(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo val title : String?,
    @ColumnInfo val text : String?,
)
