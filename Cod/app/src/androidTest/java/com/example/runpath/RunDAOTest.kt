package com.example.runpath

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.runpath.database.RunDAO
import com.example.runpath.models.Run
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RunDAOTest {

    private lateinit var runDAO: RunDAO
    private val db = FirebaseFirestore.getInstance()
    private val userId = "testUser123"
    private var insertedRunId: String? = null // Firestore-assigned runId

    @Before
    fun setup() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
        runDAO = RunDAO()
        db.useEmulator("10.0.2.2", 8080) // Firestore Emulator
    }

    @After
    fun cleanup() {
        runBlocking {
            insertedRunId?.let {
                db.collection("runs").document(it).delete().await()
            }
        }
    }

    /**
     * Test inserting a run
     */
    @Test
    fun insertRunShouldStoreRunInFirestore() = runBlocking {
        val testRun = Run(
            runId = "", // 🔹 Firestore generates the ID
            userId = userId,
            circuitId = "circuit1",
            startTime = "2025-01-29T10:00:00",
            endTime = "2025-01-29T10:30:00",
            pauseTime = "00:05:00",
            timeTracker = "00:25:00",
            paceTracker = 6.5,
            distanceTracker = 5.0
        )

        runDAO.insertRun(testRun) { insertedRun ->
            insertedRunId = insertedRun.runId // Capture Firestore ID
        }

        kotlinx.coroutines.delay(4000) // Ensure Firestore updates before checking

        assertNotNull("Firestore did not generate a runId!", insertedRunId)

        val storedRun = db.collection("runs").document(insertedRunId!!).get().await().toObject(Run::class.java)

        assertNotNull("Run was not stored in Firestore!", storedRun)
        assertEquals(testRun.userId, storedRun?.userId)
        assertEquals(testRun.paceTracker, storedRun!!.paceTracker, 0.01)
        assertEquals(testRun.distanceTracker, storedRun.distanceTracker, 0.01)
    }

    /**
     * Test retrieving a run by ID
     */
    @Test
    fun getRunByIdShouldReturnCorrectRun() = runBlocking {
        val testRun = Run(
            runId = "",
            userId = userId,
            circuitId = "circuit2",
            startTime = "2025-01-29T10:15:00",
            endTime = "2025-01-29T10:45:00",
            pauseTime = "00:03:00",
            timeTracker = "00:27:00",
            paceTracker = 6.8,
            distanceTracker = 4.5
        )

        runDAO.insertRun(testRun) { insertedRun ->
            insertedRunId = insertedRun.runId
        }

        kotlinx.coroutines.delay(4000)

        assertNotNull("Firestore did not generate a runId!", insertedRunId)

        val retrievedRun = db.collection("runs").document(insertedRunId!!).get().await().toObject(Run::class.java)

        assertNotNull("Run was not found in Firestore!", retrievedRun)
        assertEquals(testRun.circuitId, retrievedRun?.circuitId)
        assertEquals(testRun.paceTracker, retrievedRun!!.paceTracker, 0.01)
        assertEquals(testRun.distanceTracker, retrievedRun.distanceTracker, 0.01)
    }

    /**
     * Test updating a run
     */
    @Test
    fun updateRunShouldModifyExistingRun() = runBlocking {
        val testRun = Run(
            runId = "",
            userId = userId,
            circuitId = "circuit1",
            startTime = "2025-01-29T10:00:00",
            endTime = "2025-01-29T10:30:00",
            pauseTime = "00:05:00",
            timeTracker = "00:25:00",
            paceTracker = 6.5,
            distanceTracker = 5.0
        )

        runDAO.insertRun(testRun) { insertedRun ->
            insertedRunId = insertedRun.runId
        }

        kotlinx.coroutines.delay(4000)

        assertNotNull("Firestore did not generate a runId!", insertedRunId)

        runDAO.updateRun(insertedRunId!!, userId, "circuit3", "2025-01-29T10:10:00", "2025-01-29T10:40:00", "00:02:00", "00:28:00", 6.2, 5.5)
        kotlinx.coroutines.delay(4000)

        val updatedRun = db.collection("runs").document(insertedRunId!!).get().await().toObject(Run::class.java)

        assertNotNull("Run was not updated in Firestore!", updatedRun)
        assertEquals("circuit3", updatedRun?.circuitId)
        assertEquals(6.2, updatedRun!!.paceTracker, 0.01)
        assertEquals(5.5, updatedRun.distanceTracker, 0.01)
    }

    /**
     * Test deleting a run
     */
    @Test
    fun deleteRunShouldRemoveRunFromFirestore() = runBlocking {
        val testRun = Run(
            runId = "",
            userId = userId,
            circuitId = "circuit4",
            startTime = "2025-01-29T11:00:00",
            endTime = "2025-01-29T11:30:00",
            pauseTime = "00:02:30",
            timeTracker = "00:27:30",
            paceTracker = 7.0,
            distanceTracker = 6.2
        )

        runDAO.insertRun(testRun) { insertedRun ->
            insertedRunId = insertedRun.runId
        }

        kotlinx.coroutines.delay(4000)

        assertNotNull("Firestore did not generate a runId!", insertedRunId)

        runDAO.deleteRun(insertedRunId!!)
        kotlinx.coroutines.delay(4000)

        val storedRun = db.collection("runs").document(insertedRunId!!).get().await()

        assertFalse("Run was not deleted from Firestore!", storedRun.exists())
    }

}