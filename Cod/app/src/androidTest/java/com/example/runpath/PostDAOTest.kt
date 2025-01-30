package com.example.runpath

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso
import com.example.runpath.models.Post
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

class PostDAO {
    private val db = FirebaseFirestore.getInstance()

    init {
        if (isRunningTest()) {
            db.useEmulator("10.0.2.2", 8080) // ✅ Use Firestore Emulator during tests
        }
    }

    private fun isRunningTest(): Boolean {
        return try {
            Class.forName("androidx.test.espresso.Espresso")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    // Insert a new post
    fun insertPost(post: Post, onComplete: (Post) -> Unit) {
        val documentReference = db.collection("posts").document()
        val postId = documentReference.id
        val newPost = post.copy(postId = postId)

        documentReference.set(newPost)
            .addOnSuccessListener {
                onComplete(newPost)
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

    // Retrieve all posts
    fun getPosts(onComplete: (List<Post>) -> Unit) {
        db.collection("posts")
            .get()
            .addOnSuccessListener { documents ->
                val posts = documents.map { it.toObject(Post::class.java).copy(postId = it.id) }
                onComplete(posts)
            }
            .addOnFailureListener { e -> println("Error getting documents: $e") }
    }

    // Listen for post changes
    fun listenForPosts(onPostsUpdated: (List<Post>) -> Unit): ListenerRegistration {
        return db.collection("posts")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    println("Listen failed: $e")
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val posts = snapshots.map { it.toObject(Post::class.java).copy(postId = it.id) }
                    onPostsUpdated(posts)
                }
            }
    }

    // Delete a post
    fun deletePost(postId: String) {
        db.collection("posts")
            .document(postId)
            .delete()
            .addOnSuccessListener { println("Post deleted!") }
            .addOnFailureListener { e -> println("Error deleting post: $e") }
    }
}

@RunWith(AndroidJUnit4::class)
class PostDAOTest {

    private lateinit var postDAO: PostDAO
    private val db = FirebaseFirestore.getInstance()

    @Before
    fun setup() {
        FirebaseApp.initializeApp(androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext)
        postDAO = PostDAO()

        db.useEmulator("10.0.2.2", 8080)
    }

    // Test insertion
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

    // Test retrieving a post by its ID
    @Test
    fun getPostByIdShouldReturnCorrectPost() = kotlinx.coroutines.runBlocking {
        val testPost = Post(
            userId = "123",
            postId = "",
            author = "TestUser",
            timestamp = "2025-01-29",
            content = "Test Retrieve Content",
            communityId = "456"
        )

        var postId: String? = null
        postDAO.insertPost(testPost) {insertedPost ->
            postId = insertedPost.postId
        }

        kotlinx.coroutines.delay(3000)

        val retrievedDoc = db.collection("posts").document(postId!!).get().await()
        val storedPost = retrievedDoc.toObject(Post::class.java)

        assertNotNull(storedPost)
        assertEquals(testPost.content, storedPost?.content)
    }

    // Test retrieving all posts
    @Test
    fun getPostsShouldReturnAllPosts() = kotlinx.coroutines.runBlocking {
        val testPost1 = Post(
            userId = "111",
            postId = "",
            author = "User1",
            timestamp = "2025-01-29",
            content = "First Test Post",
            communityId = "456"
        )

        val testPost2 = Post(
            userId = "222",
            postId = "",
            author = "User2",
            timestamp = "2025-01-30",
            content = "Second Test Post",
            communityId = "789"
        )

        postDAO.insertPost(testPost1) {}
        postDAO.insertPost(testPost2) {}

        kotlinx.coroutines.delay(3000)

        postDAO.getPosts { posts ->
            assertTrue(posts.isNotEmpty())
            assertEquals(2, posts.size)     // Ensure that both posts are retrieved
        }
    }

    // Test deleting a post
    @Test
    fun deletePostShouldRemovePostFromFirestore() = kotlinx.coroutines.runBlocking {
        val testPost = Post(
            userId = "123",
            postId = "",
            author = "TestUser",
            timestamp = "2025-01-29",
            content = "Test Delete Content",
            communityId = "456"
        )

        var postId: String? = null
        postDAO.insertPost(testPost) {insertedPost ->
            postId = insertedPost.postId
        }

        kotlinx.coroutines.delay(2000)

        postDAO.deletePost(postId!!)

        kotlinx.coroutines.delay(2000)

        val retrievedDoc = db.collection("posts").document(postId!!).get().await()
        assertFalse(retrievedDoc.exists())      //      Post should not exist after deletion
    }
}