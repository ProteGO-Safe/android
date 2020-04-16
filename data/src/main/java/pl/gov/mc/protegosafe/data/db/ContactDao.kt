package pl.gov.mc.protegosafe.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import pl.gov.mc.protegosafe.data.entity.Contact

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(contact: Contact)

    @Delete
    fun delete(contact: Contact)

    @Update
    fun update(contact: Contact)

    @Query("SELECT * FROM CONTACTS_TABLE WHERE hash == (:hash) AND status == 1 LIMIT 1")
    fun getContactByHashIfNotLost(hash: String) : Contact?

    @Query("SELECT * FROM CONTACTS_TABLE WHERE hash == (:hash) LIMIT 1")
    fun getContactByHash(hash: String) : Contact?

    @Query("SELECT * FROM CONTACTS_TABLE ORDER BY match_timestamp DESC")
    fun getContacts() : List<Contact>

    @Query("SELECT COUNT(*) FROM CONTACTS_TABLE")
    fun count() : Int

    @Query("SELECT * FROM CONTACTS_TABLE")
    fun getAllContacts(): List<Contact>
}