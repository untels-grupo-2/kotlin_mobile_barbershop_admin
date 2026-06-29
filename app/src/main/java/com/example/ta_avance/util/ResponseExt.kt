package com.example.ta_avance.util

import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

fun Response<*>.errorMessage(): String {
    return try {
        val body = errorBody()?.string()
        if (!body.isNullOrBlank()) {
            JSONObject(body).optString("message").takeIf { it.isNotBlank() } ?: "Error ${code()}"
        } else {
            "Error ${code()}"
        }
    } catch (e: JSONException) {
        "Error ${code()}"
    }
}
