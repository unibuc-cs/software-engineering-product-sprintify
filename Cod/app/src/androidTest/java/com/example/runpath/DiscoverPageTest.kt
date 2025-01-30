package com.example.runpath

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.runpath.models.Community
import com.example.runpath.ui.theme.ProfileAndCommunity.DiscoverPage
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters


@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DiscoverPageTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<androidx.activity.ComponentActivity>()

    //  Test if UI components exist
    @Test
    fun testA_testDiscoverPageUIComponents() {
        composeTestRule.setContent {
            DiscoverPage(userId = "testUser", navController = TestNavHostController(composeTestRule.activity))
        }

        // Verify "Available Communities" and "Joined Communities" sections exist
        composeTestRule.onNodeWithText("Available Communities").assertExists()
        composeTestRule.onNodeWithText("Joined Communities").assertExists()

        // Verify "Create New Community" button exists
        composeTestRule.onNodeWithText("Create New Community").assertExists()
    }

    //  Test if "Create New Community" button opens a dialog
    @Test
    fun testB_testCreateCommunityDialogOpens() {
        composeTestRule.setContent {
            DiscoverPage(userId = "testUser", navController = TestNavHostController(composeTestRule.activity))
        }

        // Click on "Create New Community" button
        composeTestRule.onNodeWithText("Create New Community").performClick()

        // Check if dialog UI elements are displayed
        composeTestRule.onNodeWithText("Community Name").assertExists()
        composeTestRule.onNodeWithText("Description").assertExists()
        composeTestRule.onNodeWithText("Create").assertExists()
    }

    //  Test if user can join and leave a community
    @Test
    fun testC_testJoinAndLeaveCommunity() {
        val testCommunities = listOf(
            Community(communityId = "3aLfaipiUG7yND5NDYaW", name = "1"),
            Community(communityId = "hNe8sD9ZwwTwaWvvTGm5", name = "2")
        )

        composeTestRule.setContent {
            DiscoverPage(
                userId = "testUser",
                navController = TestNavHostController(composeTestRule.activity)
            )
        }

        // Wait for UI to load
        composeTestRule.waitForIdle()
        Thread.sleep(2000)

        // Click "Join" button on the first community
        composeTestRule.onAllNodesWithText("Join").onFirst().performClick()

        composeTestRule.waitForIdle()
        Thread.sleep(2000)

        // Verify that "Leave Community" button appears (indicating successful join)
        composeTestRule.onAllNodesWithContentDescription("Leave Community").onFirst().assertExists()

        // Click "Leave Community" button
        composeTestRule.onAllNodesWithContentDescription("Leave Community").onFirst().performClick()

        composeTestRule.waitForIdle()
        Thread.sleep(2000)

        // Ensure "Join" button appears again after leaving
        composeTestRule.onAllNodesWithText("Join").onFirst().assertExists()
        composeTestRule.onAllNodesWithText("Join").onFirst().performClick()

        // Ensure to leave the community after the test so the next test can be runned

        composeTestRule.onAllNodesWithContentDescription("Leave Community").onFirst().performClick()
    }

    //  Test if communities are displayed correctly
    @Test
    fun testD_testCommunityListDisplaysCommunities() {
        val testCommunities = listOf(
            Community(communityId = "3aLfaipiUG7yND5NDYaW", name = "1"),
            Community(communityId = "hNe8sD9ZwwTwaWvvTGm5", name = "2")
        )

        composeTestRule.setContent {
            DiscoverPage(
                userId = "testUser",
                navController = TestNavHostController(composeTestRule.activity)
            )
        }

        // Wait for UI to load
        composeTestRule.waitForIdle()
        Thread.sleep(2000)

        // Check if test community names are displayed
        composeTestRule.onAllNodesWithText("1").assertCountEquals(1)
        composeTestRule.onAllNodesWithText("2").assertCountEquals(1)
    }
}
