package com.example.runpath

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.runpath.models.Post
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.example.runpath.database.PostDAO

@RunWith(AndroidJUnit4::class)
class PostDAOTest {

    private lateinit var postDAO: PostDAO
    private val db = FirebaseFirestore.getInstance()

    @Before
    fun setup() {
        FirebaseApp.initializeApp(androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext)
        postDAO = PostDAO()
    }

    @Test
    fun insertPostShouldStorePostInFirestore() = kotlinx.coroutines.runBlocking {
        // Create a test post with required attributes
        val testPost = Post(
            userId = "123",
            postId = "",
            author = "Author Test",
            timestamp = "2025-01-29",
            content = "Test Content",
            communityId = "456"
        )

        // Insert post
        var insertedPostId: String? = null
        postDAO.insertPost(testPost) { insertedPost ->
            insertedPostId = insertedPost.postId
        }

        // Wait for Firestore to update
        kotlinx.coroutines.delay(3000)

        // Retrieve the post from Firestore
        val retrievedDoc = db.collection("posts").document(insertedPostId!!).get().await()
        val storedPost = retrievedDoc.toObject(Post::class.java)

        // Assertions - Verify required attributes
        assertEquals(testPost.userId, storedPost?.userId)
        assertEquals(testPost.author, storedPost?.author)
        assertEquals(testPost.timestamp, storedPost?.timestamp)
        assertEquals(testPost.content, storedPost?.content)
        assertEquals(testPost.communityId, storedPost?.communityId)
    }
}