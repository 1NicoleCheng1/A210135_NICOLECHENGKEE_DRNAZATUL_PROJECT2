package com.example.a210135_nicolechengkee_drnazatul_project2.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

//ROOM ENTITIES

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Nicole Cheng",
    val email: String = "nicole@gmail.com",
    val phone: String = "",
    val location: String = "Selangor, Malaysia",
    val skills: String = "HTML,CSS,JavaScript,UI/UX",
    val photoUri: String? = null
)

@Entity(tableName = "saved_job")
data class SavedJobEntity(
    @PrimaryKey val jobId: String,
    val title: String,
    val company: String,
    val location: String,
    val salary: String,
    val jobType: String = "Full Time",
    val description: String = "",
    val jobUrl: String = "",
    val savedAt: Long = System.currentTimeMillis(),
    val isBookmarked: Boolean = false
)

@Entity(tableName = "application")
data class ApplicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val jobId: String,
    val jobTitle: String,
    val company: String,
    val coverLetter: String,
    val appliedAt: Long = System.currentTimeMillis(),
    val status: String = "Pending"
)

//FIRESTORE MODEL

data class CommunityPost(
    val docId: String = "",
    val authorName: String = "",
    val content: String = "",
    val category: String = "General",
    val likes: Int = 0,
    val timestamp: Timestamp? = null
)

// REMOTIVE API RESPONSE

data class RemotiveResponse(val jobs: List<RemotiveJob> = emptyList())

data class RemotiveJob(
    val id: Int = 0,
    val title: String = "",
    val company_name: String = "",
    val candidate_required_location: String = "",
    val salary: String = "",
    val job_type: String = "full_time",
    val description: String = "",
    val url: String = "",
    val company_logo: String? = null
) {
    fun toEntity() = SavedJobEntity(
        jobId       = "api_$id",
        title       = title,
        company     = company_name,
        location    = "Selangor, Malaysia",
        salary      = convertToRM(salary),
        jobType     = when (job_type.lowercase().trim()) {
            "full_time"  -> "Full Time"
            "part_time"  -> "Part Time"
            "contract"   -> "Contract"
            "freelance"  -> "Freelance"
            "internship" -> "Internship"
            "other"      -> "Other"
            else         -> job_type
        },
        description = android.text.Html.fromHtml(
            description,
            android.text.Html.FROM_HTML_MODE_COMPACT
        ).toString().take(400),
        jobUrl      = url
    )

    private fun convertToRM(raw: String): String {
        if (raw.isBlank()) return malaysianSalaryPlaceholder()

        val cleaned  = raw.replace(",", "").replace(" ", "")
        val regex    = Regex("""(\d+(?:\.\d+)?)""")
        val matches  = regex.findAll(cleaned).map { it.value.toDoubleOrNull() ?: 0.0 }.toList()

        if (matches.isEmpty()) return malaysianSalaryPlaceholder()

        val usdRate  = 4.7
        val rmMin    = (matches.first() * usdRate).toLong()
        val rmMax    = if (matches.size >= 2) (matches[1] * usdRate).toLong() else null

        return if (rmMax != null && rmMax > rmMin) {
            "RM ${formatRM(rmMin)} - RM ${formatRM(rmMax)}"
        } else {
            "RM ${formatRM(rmMin)}"
        }
    }

    private fun formatRM(value: Long): String {
        return when {
            value >= 1_000_000 -> "${value / 1_000_000}M"
            value >= 1_000     -> "${value / 1_000},${String.format("%03d", value % 1_000)}"
            else               -> value.toString()
        }
    }

    private fun malaysianSalaryPlaceholder(): String {
        return when (job_type.lowercase().trim()) {
            "part_time"  -> "RM 1,500 - RM 3,000"
            "contract"   -> "RM 4,000 - RM 7,000"
            "freelance"  -> "RM 2,000 - RM 5,000"
            "internship" -> "RM 800 - RM 1,500"
            else         -> "RM 3,000 - RM 8,000"
        }
    }
}

// CHAT MODELS

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: String
)

data class RecruiterChat(
    val name: String,
    val company: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int = 0,
    val jobTitle: String
)

// UI HELPER

data class JobUi(
    val id: String,
    val title: String,
    val company: String,
    val location: String,
    val salary: String,
    val type: String = "Full Time",
    val description: String = "",
    val url: String = "",
    val logoUrl: String? = null
)

fun SavedJobEntity.toUi() = JobUi(
    id          = jobId,
    title       = title,
    company     = company,
    location    = location,
    salary      = salary,
    type        = jobType,
    description = description,
    url         = jobUrl
)