package com.example.a210135_nicolechengkee_drnazatul_project2.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// UserProfile

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<UserProfileEntity?>

    @Upsert
    suspend fun save(profile: UserProfileEntity)
}

//SavedJob DAO

@Dao
interface SavedJobDao {
    @Query("SELECT * FROM saved_job ORDER BY savedAt DESC")
    fun getAllJobs(): Flow<List<SavedJobEntity>>

    @Query("SELECT * FROM saved_job WHERE isBookmarked = 1 ORDER BY savedAt DESC")
    fun getBookmarked(): Flow<List<SavedJobEntity>>

    @Query("SELECT * FROM saved_job WHERE jobId = :id")
    suspend fun getById(id: String): SavedJobEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(jobs: List<SavedJobEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: SavedJobEntity)

    @Query("UPDATE saved_job SET isBookmarked = :bookmarked WHERE jobId = :id")
    suspend fun setBookmark(id: String, bookmarked: Boolean)

    @Query("DELETE FROM saved_job WHERE isBookmarked = 0")
    suspend fun clearCache()
}

// Application DAO

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM application ORDER BY appliedAt DESC")
    fun getAll(): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM application WHERE jobId = :jobId LIMIT 1")
    suspend fun getByJobId(jobId: String): ApplicationEntity?

    @Query("SELECT jobId FROM application")
    suspend fun getAllAppliedJobIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(application: ApplicationEntity)
}
