package com.example.ta_avance.data.local.dao

import androidx.room.*
import com.example.ta_avance.data.local.entity.BarberoEntity

@Dao
interface BarberoDao {
    @Query("SELECT * FROM barberos")
    suspend fun obtenerTodos(): List<BarberoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(barberos: List<BarberoEntity>)

    @Query("DELETE FROM barberos")
    suspend fun eliminarTodos()

    @Query("SELECT MAX(cacheTimestamp) FROM barberos")
    suspend fun obtenerTimestamp(): Long?
}
