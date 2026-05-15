package com.example.ta_avance.dto.barbero

//USADO PARA EL ACTUALIZAR BARBERRO
// BarberoSimpleResponse.java
data class BarberoSimpleResponse(
    val status: Int = 0,
    val message: String? = null,
    val data: BarberoDto? = null
)

