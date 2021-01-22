package dev.yjyoon.enphago

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Word::class), version = 1, exportSchema = false)
abstract class RoomWordHelper: RoomDatabase() {
    abstract fun roomWordDAO(): RoomWordDAO
}