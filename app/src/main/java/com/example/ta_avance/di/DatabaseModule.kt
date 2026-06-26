package com.example.ta_avance.di

import android.content.Context
import androidx.room.Room
import com.example.ta_avance.data.local.AppDatabase
import com.example.ta_avance.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "barbershop_db").build()

    @Provides fun provideBarberoDao(db: AppDatabase): BarberoDao = db.barberoDao()
    @Provides fun provideServicioDao(db: AppDatabase): ServicioDao = db.servicioDao()
    @Provides fun provideReservaDao(db: AppDatabase): ReservaDao = db.reservaDao()
    @Provides fun provideValoracionDao(db: AppDatabase): ValoracionDao = db.valoracionDao()
    @Provides fun provideUsuarioDao(db: AppDatabase): UsuarioDao = db.usuarioDao()
}
