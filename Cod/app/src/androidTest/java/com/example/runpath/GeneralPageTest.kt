package com.example.runpath

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.app.ComponentActivity
import androidx.core.content.contentValuesOf
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.runpath.models.Community
import com.example.runpath.models.Post
import com.example.runpath.ui.theme.ProfileAndCommunity.GeneralPage
import okhttp3.internal.wait
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GeneralPageTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<androidx.activity.ComponentActivity>()

    // Test if UI elements exist
    @Test
    fun testGeneralPageUIComponent() {
        composeTestRule.setContent {
            GeneralPage(userId = "123", username = "TestUser", navController = TestNavHostController(composeTestRule.activity))
        }

        // Check if "Community" title is displayed
        composeTestRule.onNodeWithText("Community").assertExists()

        // Check if "Create Post" button exists
        composeTestRule.onNodeWithText("Create Post").assertExists()
    }

    // Test creating a post
    @Test
    fun testCreatePostDialog() {
        val navController = TestNavHostController(composeTestRule.activity)

        val testCommunities = listOf(
            Community(communityId = "1", name = "Test Community"),
            Community(communityId = "2", name = "Another Community")
        )

        composeTestRule.setContent {
            GeneralPage(
                userId = "123",
                username = "TestUser",
                navController = navController,
                mockJoinedCommunities = testCommunities
            )
        }

        // Click on create post button
        composeTestRule.onNodeWithText("Create Post").performClick()

        // Check if the dialog is displayed
        composeTestRule.onNodeWithText("Post Content").assertExists()

        // Type into the text field
        composeTestRule.onNode(hasText("Post Content")).performTextInput("This is a test post")

        // Click on "Select a community"
        composeTestRule.onNodeWithText("Select a community").performClick()

        composeTestRule.onNodeWithText("Test Community").assertExists()
        composeTestRule.onNodeWithText("Another Community").assertExists()

        composeTestRule.onNode(hasText("This is a test post")).assertExists()

        // Click on "Create post" button
        composeTestRule.onNodeWithText("Add Post").assertExists()
    }

    // Test if posts are displayed
    @Test
    fun testPostListDisplaysPosts() {
        val navController = TestNavHostController(composeTestRule.activity)

        val testCommunities = listOf(
            Community(communityId = "1", name = "Test Community"),
            Community(communityId = "2", name = "Another Community")
        )

        val testPosts = listOf(
            Post(userId = "123", author = "TestUser", content = "First post", timestamp = "2024-01-01T12:00:00", communityId = "1"),
            Post(userId = "124", author = "UserB", content = "Second post", timestamp = "2024-01-02T14:30:00", communityId = "2")
        )

        composeTestRule.setContent {
            GeneralPage(
                userId = "123",
                username = "TestUser",
                navController = navController,
                mockJoinedCommunities = testCommunities, // Inject mock communities
                mockPosts = testPosts // Inject mock posts
            )
        }

        composeTestRule.waitForIdle()

        // Ensure the first post is displayed
        composeTestRule.onNode(hasText("First post", substring = true)).assertExists()
    }

    // Test deleting a post
    @Test
    fun testDeletePost() {
        val navController = TestNavHostController(composeTestRule.activity)

        val testCommunities = listOf(
            Community(communityId = "1", name = "Test Community")
        )

        val testPosts = listOf(
            Post(
                postId = "post1",
                userId = "123",
                author = "TestUser",
                content = "Deletable post",
                timestamp = "2024-01-01T12:00:00",
                communityId = "1"
            )
        )

        composeTestRule.setContent {
            GeneralPage(
                userId = "123",
                username = "TestUser",
                navController = navController,
                mockJoinedCommunities = testCommunities,
                mockPosts = testPosts
            )
        }

        // Ensure UI is fully drawn before searching for posts
        composeTestRule.waitForIdle()

        // Verify the post exists before deletion
        composeTestRule.onNode(hasText("Deletable post", substring = true)).assertExists()

        // Click the "Delete Post" button
        composeTestRule.onNodeWithContentDescription("Delete Post").performClick()

        // Wait until the post disappears from the UI
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Deletable post").fetchSemanticsNodes().isEmpty()
        }

        // Ensure the post is no longer displayed
        composeTestRule.onNodeWithText("Deletable post").assertDoesNotExist()
    }
}