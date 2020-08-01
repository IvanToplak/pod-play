package hr.from.ivantoplak.podplay.db

import androidx.room.*
import java.util.*

@Entity(
    tableName = "episode",
    foreignKeys = [ForeignKey(
        entity = DbPodcast::class,
        parentColumns = ["id"],
        childColumns = ["podcastId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("guid", unique = true), Index("podcastId")]
)
data class DbEpisode(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(defaultValue = "") val guid: String = "",
    val podcastId: Int = 0,
    @ColumnInfo(defaultValue = "") val title: String = "",
    @ColumnInfo(defaultValue = "") val description: String = "",
    @ColumnInfo(defaultValue = "") val mediaUrl: String = "",
    @ColumnInfo(defaultValue = "") val mimeType: String = "",
    val releaseDate: Date? = null,
    @ColumnInfo(defaultValue = "") val duration: String = ""
)