package dev.yjyoon.enphago

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word")
class Word {
    @PrimaryKey
    @ColumnInfo
    var word: String

    @ColumnInfo
    var first: String

    @ColumnInfo
    var end: Boolean = false

    constructor(first: String, word: String, end:Boolean){
        this.first = first
        this.word = word
        this.end = end
    }
}