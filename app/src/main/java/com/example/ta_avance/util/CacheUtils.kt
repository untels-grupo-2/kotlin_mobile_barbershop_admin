package com.example.ta_avance.util

object CacheUtils {
    private const val TTL_MS = 5 * 60 * 1000L

    fun esCacheValida(timestamp: Long?): Boolean =
        timestamp != null && System.currentTimeMillis() - timestamp < TTL_MS
}
