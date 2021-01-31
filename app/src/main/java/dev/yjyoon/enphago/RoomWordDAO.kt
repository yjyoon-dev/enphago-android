package dev.yjyoon.enphago

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface RoomWordDAO {
    @Query("SELECT * FROM word WHERE first = :tail")
    fun getWord(tail: String): MutableList<Word>

    @Query("SELECT * FROM word")
    fun getAll(): List<Word>

    @Query("SELECT * FROM word WHERE word = :word")
    fun findWord(word: String): List<Word>

    @Insert(onConflict = REPLACE)
    fun insert(word: Word)

    @Delete
    fun delete(word: Word)

    @Query("DELETE FROM word")
    fun deleteAll()
}