package hr.from.ivantoplak.podplay.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "podcast",
    indices = [Index("feedUrl", unique = true)]
)
data class DbPodcast(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(defaultValue = "") val feedUrl: String = "",
    @ColumnInfo(defaultValue = "") val feedTitle: String = "",
    @ColumnInfo(defaultValue = "") val feedDesc: String = "",
    @ColumnInfo(defaultValue = "") val imageUrl: String = "",
    @ColumnInfo(defaultValue = "") val artworkUrl: String = "",
    @ColumnInfo(defaultValue = "") val category: String = ""
)
