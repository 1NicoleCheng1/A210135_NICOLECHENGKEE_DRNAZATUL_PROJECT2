package com.example.a210135_nicolechengkee_drnazatul_project2.data

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object CommunityRepository {

    private val db = Firebase.firestore
    private val col get() = db.collection("community_posts")

    //Real-time stream of community posts
    fun getPosts(): Flow<List<CommunityPost>> = callbackFlow {
        val listener = col
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Don't close the flow — just send empty list so app doesn't crash
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(CommunityPost::class.java)?.copy(docId = doc.id)
                } ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }
    suspend fun addPost(post: CommunityPost) {
        col.add(
            hashMapOf(
                "authorName" to post.authorName,
                "content"    to post.content,
                "category"   to post.category,
                "likes"      to 0,
                "timestamp"  to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )
        ).await()
    }

    suspend fun likePost(docId: String) {
        col.document(docId).update(
            "likes", com.google.firebase.firestore.FieldValue.increment(1)
        ).await()
    }
}
