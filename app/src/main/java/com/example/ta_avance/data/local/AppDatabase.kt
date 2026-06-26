package com.example.ta_avance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ta_avance.data.local.dao.*
import com.example.ta_avance.data.local.entity.*

@Database(
    entities = [
        BarberoEntity::class,
        ServicioEntity::class,
        ReservaEntity::class,
        ValoracionEntity::class,
        UsuarioEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun barberoDao(): BarberoDao
    abstract fun servicioDao(): ServicioDao
    abstract fun reservaDao(): ReservaDao
    abstract fun valoracionDao(): ValoracionDao
    abstract fun usuarioDao(): UsuarioDao
}
