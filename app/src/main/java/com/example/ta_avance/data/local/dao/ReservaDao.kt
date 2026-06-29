package com.example.ta_avance.data.local.dao

import androidx.room.*
import com.example.ta_avance.data.local.entity.ReservaEntity

@Dao
interface ReservaDao {
    @Query("SELECT * FROM reservas WHERE cacheKey = :cacheKey")
    suspend fun obtenerPorClave(cacheKey: String): List<ReservaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(reservas: List<ReservaEntity>)

    @Query("DELETE FROM reservas WHERE cacheKey = :cacheKey")
    suspend fun eliminarPorClave(cacheKey: String)

    @Query("DELETE FROM reservas")
    suspend fun eliminarTodos()

    @Query("SELECT MAX(cacheTimestamp) FROM reservas WHERE cacheKey = :cacheKey")
    suspend fun obtenerTimestamp(cacheKey: String): Long?
}
