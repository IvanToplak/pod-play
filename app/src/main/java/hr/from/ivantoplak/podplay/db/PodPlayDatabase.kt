package hr.from.ivantoplak.podplay.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import hr.from.ivantoplak.podplay.db.PodPlayDatabase.Companion.VERSION

@Database(entities = [DbPodcast::class, DbEpisode::class], version = VERSION, exportSchema = false)
@TypeConverters(DbConverters::class)
abstract class PodPlayDatabase : RoomDatabase() {

    companion object {
        const val NAME = "podPlay"
        const val VERSION = 1
    }

    abstract fun podcastDao(): PodcastDao
}