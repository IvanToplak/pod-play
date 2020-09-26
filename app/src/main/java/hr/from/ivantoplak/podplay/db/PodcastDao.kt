package hr.from.ivantoplak.podplay.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PodcastDao {

    @Query("SELECT * FROM Podcast WHERE feedUrl = :url")
    suspend fun loadPodcast(url: String): DbPodcast?

    @Query("SELECT * FROM Podcast ORDER BY id")
    fun loadPodcasts(): Flow<List<DbPodcast>>

    @Query("SELECT * FROM Podcast ORDER BY id")
    suspend fun loadPodcastsStatic(): List<DbPodcast>

    @Query("SELECT * FROM Episode WHERE podcastId = :podcastId ORDER BY releaseDate DESC")
    suspend fun loadEpisodes(podcastId: Int): List<DbEpisode>

    @Insert(onConflict = REPLACE)
    suspend fun insertPodcast(podcast: DbPodcast): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertEpisodes(episodes: List<DbEpisode>)

    @Delete
    suspend fun deletePodcast(podcast: DbPodcast)
}