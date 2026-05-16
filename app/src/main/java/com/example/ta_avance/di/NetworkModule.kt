package com.example.ta_avance.di

import android.content.Context
import com.example.ta_avance.BuildConfig
import com.example.ta_avance.api.AuthApiService
import com.example.ta_avance.api.AuthInterceptor
import com.example.ta_avance.api.service.*
import com.example.ta_avance.util.PreferenciasHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providePreferenciasHelper(
        @ApplicationContext context: Context
    ): PreferenciasHelper = PreferenciasHelper(context)

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        @ApplicationContext context: Context
    ): AuthInterceptor = AuthInterceptor(context)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideAuthApiServiceKt(retrofit: Retrofit): AuthApiServiceKt =
        retrofit.create(AuthApiServiceKt::class.java)

    @Provides
    @Singleton
    fun provideBarberoApiService(retrofit: Retrofit): BarberoApiService =
        retrofit.create(BarberoApiService::class.java)

    @Provides
    @Singleton
    fun provideServicioApiService(retrofit: Retrofit): ServicioApiService =
        retrofit.create(ServicioApiService::class.java)

    @Provides
    @Singleton
    fun provideHorarioApiService(retrofit: Retrofit): HorarioApiService =
        retrofit.create(HorarioApiService::class.java)

    @Provides
    @Singleton
    fun provideReservaApiService(retrofit: Retrofit): ReservaApiService =
        retrofit.create(ReservaApiService::class.java)

    @Provides
    @Singleton
    fun provideUsuarioApiService(retrofit: Retrofit): UsuarioApiService =
        retrofit.create(UsuarioApiService::class.java)

    @Provides
    @Singleton
    fun provideValoracionApiService(retrofit: Retrofit): ValoracionApiService =
        retrofit.create(ValoracionApiService::class.java)

    @Provides
    @Singleton
    fun provideReporteApiService(retrofit: Retrofit): ReporteApiService =
        retrofit.create(ReporteApiService::class.java)
}