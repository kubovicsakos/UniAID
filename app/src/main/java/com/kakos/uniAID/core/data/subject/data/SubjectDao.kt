package com.kakos.uniAID.core.data.subject.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.model.SubjectWithEvents
import com.kakos.uniAID.core.domain.subject.domain.model.SubjectWithNotes
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Subject. THIS
 *
 * Provides CRUD operations for the subjects table.
 */
@Dao
interface SubjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subject: Subject): Long

    @Update
    suspend fun update(subject: Subject)

    @Delete
    suspend fun delete(subject: Subject)

    @Query("DELETE FROM subjects WHERE id = :subjectId")
    suspend fun deleteSubjectById(subjectId: Int)

    @Query("SELECT * FROM subjects WHERE id = :subjectId")
    suspend fun getSubjectById(subjectId: Int): Subject?

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE semester = :semester")
    fun getSubjectsBySemester(semester: Int): Flow<List<Subject>>

    @Transaction
    @Query("SELECT * FROM subjects WHERE id = :subjectId")
    fun getSubjectWithNotes(subjectId: Int): SubjectWithNotes

    @Transaction
    @Query("SELECT * FROM subjects WHERE id = :subjectId")
    fun getSubjectWithEvents(subjectId: Int): SubjectWithEvents
}