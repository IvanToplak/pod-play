package hr.from.ivantoplak.podplay.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface PodcastDao {

    @Query("SELECT * FROM Podcast WHERE feedUrl = :url")
    fun loadPodcast(url: String): DbPodcast?

    @Query("SELECT * FROM Podcast ORDER BY id")
    fun loadPodcasts(): LiveData<List<DbPodcast>>

    @Query("SELECT * FROM Podcast ORDER BY id")
    fun loadPodcastsStatic(): List<DbPodcast>

    @Query("SELECT * FROM Episode WHERE podcastId = :podcastId ORDER BY releaseDate DESC")
    fun loadEpisodes(podcastId: Int): List<DbEpisode>

    @Insert(onConflict = REPLACE)
    fun insertPodcast(podcast: DbPodcast): Long

    @Insert(onConflict = REPLACE)
    fun insertEpisode(episode: DbEpisode): Long

    @Insert(onConflict = REPLACE)
    fun insertEpisodes(episodes: List<DbEpisode>)

    @Delete
    fun deletePodcast(podcast: DbPodcast)
}