package com.example.ta_avance.data.local.dao

import androidx.room.*
import com.example.ta_avance.data.local.entity.ServicioEntity

@Dao
interface ServicioDao {
    @Query("SELECT * FROM servicios")
    suspend fun obtenerTodos(): List<ServicioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(servicios: List<ServicioEntity>)

    @Query("DELETE FROM servicios")
    suspend fun eliminarTodos()

    @Query("SELECT MAX(cacheTimestamp) FROM servicios")
    suspend fun obtenerTimestamp(): Long?
}
