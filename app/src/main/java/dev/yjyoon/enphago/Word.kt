package dev.yjyoon.enphago

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word")
class Word {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var no: Int? = null

    @ColumnInfo
    var first: String? = null

    @ColumnInfo
    var word: String? = null

    constructor(first: String, word: String){
        this.first = first
        this.word = word
    }
}