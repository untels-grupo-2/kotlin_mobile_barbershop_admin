package com.example.ta_avance.data.local.dao

import androidx.room.*
import com.example.ta_avance.data.local.entity.ValoracionEntity

@Dao
interface ValoracionDao {
    @Query("SELECT * FROM valoraciones")
    suspend fun obtenerTodos(): List<ValoracionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(valoraciones: List<ValoracionEntity>)

    @Query("DELETE FROM valoraciones")
    suspend fun eliminarTodos()

    @Query("SELECT MAX(cacheTimestamp) FROM valoraciones")
    suspend fun obtenerTimestamp(): Long?
}
