import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.runpath.database.DataBase.UserEntry
import com.example.runpath.database.DataBase.ProfileEntry
import com.example.runpath.database.DataBase.SavedCircuitsEntry
import com.example.runpath.database.DataBase.CircuitEntry
import com.example.runpath.database.DataBase.RunEntry
import com.example.runpath.database.DataBase.LeaderboardEntry
import com.example.runpath.database.DataBase.CommunityEntry
import com.example.runpath.database.DataBase.CommunityUsersEntry

class FeedReaderDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        // Database version and name
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "RunPath.db"

        // Create table statements
        private val SQL_CREATE_USER = "CREATE TABLE ${UserEntry.TABLE_NAME} (" +
                "${UserEntry.COLUMN_USER_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${UserEntry.COLUMN_USERNAME} TEXT NOT NULL," +
                "${UserEntry.COLUMN_PASSWORD} TEXT NOT NULL," +
                "${UserEntry.COLUMN_EMAIL} TEXT," +
                "${UserEntry.COLUMN_DATE_CREATED} TEXT)"

        private val SQL_CREATE_PROFILE = "CREATE TABLE ${ProfileEntry.TABLE_NAME} (" +
                "${ProfileEntry.COLUMN_USER_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${ProfileEntry.COLUMN_PREFERRED_TERRAIN} TEXT," +
                "${ProfileEntry.COLUMN_PREFERRED_LIGHT_LEVEL} TEXT," +
                "${ProfileEntry.COLUMN_PET_OWNER} INTEGER," +
                "FOREIGN KEY(${ProfileEntry.COLUMN_USER_ID}) REFERENCES ${UserEntry.TABLE_NAME}(${UserEntry.COLUMN_USER_ID}))"

        private val SQL_CREATE_SAVED_CIRCUITS = "CREATE TABLE ${SavedCircuitsEntry.TABLE_NAME} (" +
                "${SavedCircuitsEntry.COLUMN_SAVED_CIRCUIT_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${SavedCircuitsEntry.COLUMN_USER_ID} INTEGER," +
                "${SavedCircuitsEntry.COLUMN_CIRCUIT_ID} INTEGER," +
                "FOREIGN KEY(${SavedCircuitsEntry.COLUMN_USER_ID}) REFERENCES ${UserEntry.TABLE_NAME}(${UserEntry.COLUMN_USER_ID})," +
                "FOREIGN KEY(${SavedCircuitsEntry.COLUMN_CIRCUIT_ID}) REFERENCES ${CircuitEntry.TABLE_NAME}(${CircuitEntry.COLUMN_CIRCUIT_ID}))"

        private val SQL_CREATE_CIRCUIT = "CREATE TABLE ${CircuitEntry.TABLE_NAME} (" +
                "${CircuitEntry.COLUMN_CIRCUIT_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${CircuitEntry.COLUMN_NAME} TEXT NOT NULL," +
                "${CircuitEntry.COLUMN_DESCRIPTION} TEXT," +
                "${CircuitEntry.COLUMN_DISTANCE} REAL," +
                "${CircuitEntry.COLUMN_ESTIMATED_TIME} INTEGER," +
                "${CircuitEntry.COLUMN_INTENSITY} TEXT," +
                "${CircuitEntry.COLUMN_TERRAIN} TEXT," +
                "${CircuitEntry.COLUMN_PET_FRIENDLY} INTEGER," +
                "${CircuitEntry.COLUMN_LIGHT_LEVEL} TEXT," +
                "${CircuitEntry.COLUMN_RATING} INTEGER," +
                "${CircuitEntry.COLUMN_DIFFICULTY} INTEGER)"

        private val SQL_CREATE_RUN = "CREATE TABLE ${RunEntry.TABLE_NAME} (" +
                "${RunEntry.COLUMN_RUN_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${RunEntry.COLUMN_USER_ID} INTEGER," +
                "${RunEntry.COLUMN_CIRCUIT_ID} INTEGER," +
                "${RunEntry.COLUMN_START_TIME} TEXT," +
                "${RunEntry.COLUMN_END_TIME} TEXT," +
                "${RunEntry.COLUMN_PAUSE_TIME} INTEGER," +
                "${RunEntry.COLUMN_TIME_TRACKER} INTEGER," +
                "${RunEntry.COLUMN_PACE_TRACKER} REAL," +
                "${RunEntry.COLUMN_DISTANCE_TRACKER} REAL," +
                "FOREIGN KEY(${RunEntry.COLUMN_USER_ID}) REFERENCES ${UserEntry.TABLE_NAME}(${UserEntry.COLUMN_USER_ID})," +
                "FOREIGN KEY(${RunEntry.COLUMN_CIRCUIT_ID}) REFERENCES ${CircuitEntry.TABLE_NAME}(${CircuitEntry.COLUMN_CIRCUIT_ID}))"

        private val SQL_CREATE_LEADERBOARD = "CREATE TABLE ${LeaderboardEntry.TABLE_NAME} (" +
                "${LeaderboardEntry.COLUMN_LEADERBOARD_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${LeaderboardEntry.COLUMN_CIRCUIT_ID} INTEGER," +
                "${LeaderboardEntry.COLUMN_USER_ID} INTEGER," +
                "${LeaderboardEntry.COLUMN_RANK} INTEGER," +
                "${LeaderboardEntry.COLUMN_TIME} INTEGER," +
                "FOREIGN KEY(${LeaderboardEntry.COLUMN_CIRCUIT_ID}) REFERENCES ${CircuitEntry.TABLE_NAME}(${CircuitEntry.COLUMN_CIRCUIT_ID})," +
                "FOREIGN KEY(${LeaderboardEntry.COLUMN_USER_ID}) REFERENCES ${UserEntry.TABLE_NAME}(${UserEntry.COLUMN_USER_ID}))"

        private val SQL_CREATE_COMMUNITY = "CREATE TABLE ${CommunityEntry.TABLE_NAME} (" +
                "${CommunityEntry.COLUMN_COMMUNITY_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${CommunityEntry.COLUMN_NAME} TEXT NOT NULL," +
                "${CommunityEntry.COLUMN_DESCRIPTION} TEXT)"

        private val SQL_CREATE_COMMUNITY_USERS = "CREATE TABLE ${CommunityUsersEntry.TABLE_NAME} (" +
                "${CommunityUsersEntry.COLUMN_COMMUNITY_ID} INTEGER," +
                "${CommunityUsersEntry.COLUMN_USER_ID} INTEGER," +
                "${CommunityUsersEntry.COLUMN_DATE_JOINED} TEXT," +
                "PRIMARY KEY (${CommunityUsersEntry.COLUMN_COMMUNITY_ID}, ${CommunityUsersEntry.COLUMN_USER_ID})," +
                "FOREIGN KEY(${CommunityUsersEntry.COLUMN_COMMUNITY_ID}) REFERENCES ${CommunityEntry.TABLE_NAME}(${CommunityEntry.COLUMN_COMMUNITY_ID})," +
                "FOREIGN KEY(${CommunityUsersEntry.COLUMN_USER_ID}) REFERENCES ${UserEntry.TABLE_NAME}(${UserEntry.COLUMN_USER_ID}))"

    }
    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(SQL_CREATE_USER)
            db.execSQL(SQL_CREATE_PROFILE)
            db.execSQL(SQL_CREATE_SAVED_CIRCUITS)
            db.execSQL(SQL_CREATE_CIRCUIT)
            db.execSQL(SQL_CREATE_RUN)
            db.execSQL(SQL_CREATE_LEADERBOARD)
            db.execSQL(SQL_CREATE_COMMUNITY)
            db.execSQL(SQL_CREATE_COMMUNITY_USERS)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // to do change the onUpgrade so it does not simply recreate the database
        //onCreate(db)


    }
}

