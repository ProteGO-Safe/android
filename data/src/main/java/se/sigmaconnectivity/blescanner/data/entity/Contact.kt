package se.sigmaconnectivity.blescanner.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts_table")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "hash") val hash: String,
    @ColumnInfo(name = "status") val status: Int,
    @ColumnInfo(name = "match_timestamp") val matchTimestamp: Long,
    @ColumnInfo(name = "last_timestamp") val lostTimestamp: Long,
    @ColumnInfo(name = "duration") val duration: Long
)