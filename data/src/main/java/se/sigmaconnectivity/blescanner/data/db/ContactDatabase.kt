package se.sigmaconnectivity.blescanner.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import se.sigmaconnectivity.blescanner.data.entity.Contact

@Database(entities = [Contact::class], version = 2)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao

    companion object {
        fun buildDataBase(context: Context) = Room.databaseBuilder(
            context,
            ContactDatabase::class.java,
            "KoronaDatabase"
        ).fallbackToDestructiveMigration()
            .build()
    }
}