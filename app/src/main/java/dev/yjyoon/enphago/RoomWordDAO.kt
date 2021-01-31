package dev.yjyoon.enphago

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

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

    @Update
    fun update(word:Word)

    @Delete
    fun delete(word: Word)

    @Query("DELETE FROM word")
    fun deleteAll()
}