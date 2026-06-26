package com.example.ta_avance.data.local.dao

import androidx.room.*
import com.example.ta_avance.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios")
    suspend fun obtenerTodos(): List<UsuarioEntity>

    @Query("SELECT * FROM usuarios WHERE usuario_id = :id")
    suspend fun obtenerPorId(id: Long): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(usuarios: List<UsuarioEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: UsuarioEntity)

    @Query("DELETE FROM usuarios")
    suspend fun eliminarTodos()

    @Query("SELECT MAX(cacheTimestamp) FROM usuarios")
    suspend fun obtenerTimestamp(): Long?
}
