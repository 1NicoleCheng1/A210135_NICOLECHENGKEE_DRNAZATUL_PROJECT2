package com.example.a210135_nicolechengkee_drnazatul_project2

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.a210135_nicolechengkee_drnazatul_project2.data.*
import com.example.a210135_nicolechengkee_drnazatul_project2.network.RetrofitInstance
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class JobViewModel(app: Application) : AndroidViewModel(app) {

    //  Room DAOs
    private val db         = AppDatabase.getInstance(app)
    private val profileDao = db.userProfileDao()
    private val jobDao     = db.savedJobDao()
    private val appDao     = db.applicationDao()

    //  Profile
    val profileFlow: StateFlow<UserProfileEntity> = profileDao.getProfile()
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserProfileEntity())

    init {
        viewModelScope.launch {
            val existing = profileDao.getProfile().firstOrNull()
            if (existing == null) profileDao.save(UserProfileEntity())
            fetchRemoteJobs()
            searchRemoteJobs("Malaysia")  // pre-load Malaysia jobs on startup
        }
    }

    fun updateProfile(profile: UserProfileEntity) = viewModelScope.launch {
        profileDao.save(profile)
    }

    fun updateProfileFromScan(name: String, email: String) = viewModelScope.launch {
        val current = profileFlow.value
        profileDao.save(
            current.copy(
                name  = name.ifBlank { current.name },
                email = email.ifBlank { current.email }
            )
        )
    }

    fun updateProfilePhoto(uri: String) = viewModelScope.launch {
        profileDao.save(profileFlow.value.copy(photoUri = uri))
    }

    //  Jobs
    val allJobsFlow: StateFlow<List<SavedJobEntity>> = jobDao.getAllJobs()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val bookmarkedJobsFlow: StateFlow<List<SavedJobEntity>> = jobDao.getBookmarked()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    var isLoadingJobs by mutableStateOf(false)
    var jobsError     by mutableStateOf<String?>(null)

    fun fetchRemoteJobs(category: String = "software-dev") = viewModelScope.launch {
        isLoadingJobs = true
        jobsError     = null
        try {
            val response = RetrofitInstance.api.getJobs(category = category)
            jobDao.insertAll(response.jobs.map { it.toEntity() })
        } catch (e: Exception) {
            jobsError = "Could not load jobs: ${e.localizedMessage}"
        } finally {
            isLoadingJobs = false
        }
    }

    fun searchRemoteJobs(query: String) = viewModelScope.launch {
        if (query.isBlank()) return@launch
        isLoadingJobs = true
        jobsError     = null
        try {
            val response = RetrofitInstance.api.searchJobs(search = query)
            jobDao.insertAll(response.jobs.map { it.toEntity() })
        } catch (e: Exception) {
            jobsError = "Search failed: ${e.localizedMessage}"
        } finally {
            isLoadingJobs = false
        }
    }

    fun toggleBookmark(jobId: String, bookmarked: Boolean) = viewModelScope.launch {
        jobDao.setBookmark(jobId, bookmarked)
    }

    suspend fun getJobById(id: String): SavedJobEntity? = jobDao.getById(id)

    //  Applications
    val applicationsFlow: StateFlow<List<ApplicationEntity>> = appDao.getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun applyToJob(
        jobId: String,
        jobTitle: String,
        company: String,
        coverLetter: String
    ) = viewModelScope.launch {
        appDao.insert(
            ApplicationEntity(
                jobId       = jobId,
                jobTitle    = jobTitle,
                company     = company,
                coverLetter = coverLetter
            )
        )
    }

    suspend fun hasApplied(jobId: String): Boolean =
        appDao.getByJobId(jobId) != null

    //  Community (Firebase)
    val communityPostsFlow: StateFlow<List<CommunityPost>> =
        CommunityRepository.getPosts()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    var isPostingCommunity by mutableStateOf(false)

    fun submitCommunityPost(content: String, category: String) = viewModelScope.launch {
        isPostingCommunity = true
        try {
            CommunityRepository.addPost(
                CommunityPost(
                    authorName = profileFlow.value.name,
                    content    = content,
                    category   = category
                )
            )
        } catch (_: Exception) { }
        isPostingCommunity = false
    }

    fun likePost(docId: String) = viewModelScope.launch {
        try { CommunityRepository.likePost(docId) } catch (_: Exception) { }
    }

    // Featured Jobs
    val featuredJobsFlow: StateFlow<List<SavedJobEntity>> = allJobsFlow
        .map { it.take(5) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Recommended Jobs
    val recommendedJobsFlow: StateFlow<List<SavedJobEntity>> = allJobsFlow
        .map { list ->
            val filtered = list.filter {
                it.jobType == "Full Time" || it.jobType == "Part Time"
            }.take(6)
            filtered.ifEmpty { list.take(6) }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}