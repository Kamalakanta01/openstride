package dev.stravaoss.core

import android.app.Application
import androidx.room.Room
import dev.stravaoss.core.local.LocalDatabase

/**
 * Application entry point.
 *
 * Deliberately thin. The original app's `onCreate` starts the feed loader, the
 * remote logger, a preference migration pass and the Branch SDK — four things that
 * exist only because there is a server. Here there is nothing to start.
 */
class StravaOssApplication : Application() {

    lateinit var database: LocalDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, LocalDatabase::class.java, "strava.db")
            .build()
    }
}
