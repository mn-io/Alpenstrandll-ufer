package net.mnio.alpenstrandlaufer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AirplaneSessionDao {
    @Insert
    suspend fun insert(session: AirplaneSession)

    @Query("SELECT * FROM airplane_sessions ORDER BY id DESC")
    suspend fun getAll(): List<AirplaneSession>

    @Delete
    suspend fun delete(session: AirplaneSession)
}
