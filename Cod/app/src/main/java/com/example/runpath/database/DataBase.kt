package com.example.runpath.database

import android.provider.BaseColumns

object DataBase {
    // User table
    object UserEntry : BaseColumns {
        const val TABLE_NAME = "User"
        const val COLUMN_USER_ID = "userId"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_DATE_CREATED = "dateCreated"
    }

    // Profile table
    object ProfileEntry : BaseColumns {
        const val TABLE_NAME = "Profile"
        const val COLUMN_USER_ID = "userId" // Foreign Key
        const val COLUMN_PREFERRED_TERRAIN = "PreferredTerrain"
        const val COLUMN_PREFERRED_LIGHT_LEVEL = "PreferredLightLevel"
        const val COLUMN_PET_OWNER = "PetOwner"
    }

    // Community table
    object CommunityEntry : BaseColumns {
        const val TABLE_NAME = "Community"
        const val COLUMN_COMMUNITY_ID = "CommunityID"
        const val COLUMN_NAME = "Name"
        const val COLUMN_DESCRIPTION = "Description"
    }

    // Community_Users join table
    object CommunityUsersEntry : BaseColumns {
        const val TABLE_NAME = "Community_Users"
        const val COLUMN_COMMUNITY_ID = "CommunityID" // Foreign Key
        const val COLUMN_USER_ID = "UserID" // Foreign Key
        const val COLUMN_DATE_JOINED = "DateJoined"
    }

    object SavedCircuitsEntry : BaseColumns {
        const val TABLE_NAME = "SavedCircuits"
        const val COLUMN_SAVED_CIRCUIT_ID = "SavedCircuitID"
        const val COLUMN_USER_ID = "UserID" // Foreign Key
        const val COLUMN_CIRCUIT_ID = "CircuitID" // Foreign Key
    }

    // Circuit table
    object CircuitEntry : BaseColumns {
        const val TABLE_NAME = "Circuit"
        const val COLUMN_CIRCUIT_ID = "CircuitID"
        const val COLUMN_NAME = "Name"
        const val COLUMN_DESCRIPTION = "Description"
        const val COLUMN_DISTANCE = "Distance"
        const val COLUMN_ESTIMATED_TIME = "EstimatedTime"
        const val COLUMN_INTENSITY = "Intensity"
        const val COLUMN_TERRAIN = "Terrain"
        const val COLUMN_PET_FRIENDLY = "PetFriendly"
        const val COLUMN_LIGHT_LEVEL = "LightLevel"
        const val COLUMN_RATING = "Rating"
        const val COLUMN_DIFFICULTY = "Difficulty"
    }

    // Run table
    object RunEntry : BaseColumns {
        const val TABLE_NAME = "Run"
        const val COLUMN_RUN_ID = "RunID"
        const val COLUMN_USER_ID = "UserID" // Foreign Key
        const val COLUMN_CIRCUIT_ID = "CircuitID" // Foreign Key
        const val COLUMN_START_TIME = "StartTime"
        const val COLUMN_END_TIME = "EndTime"
        const val COLUMN_PAUSE_TIME = "PauseTime"
        const val COLUMN_TIME_TRACKER = "TimeTracker"
        const val COLUMN_PACE_TRACKER = "PaceTracker"
        const val COLUMN_DISTANCE_TRACKER = "DistanceTracker"
    }

    // Leaderboard table
    object LeaderboardEntry : BaseColumns {
        const val TABLE_NAME = "Leaderboard"
        const val COLUMN_LEADERBOARD_ID = "LeaderboardID"
        const val COLUMN_CIRCUIT_ID = "CircuitID" // Foreign Key
        const val COLUMN_USER_ID = "UserID" // Foreign Key
        const val COLUMN_RANK = "Rank"
        const val COLUMN_TIME = "Time"
    }



}
