package es.iesnervion.avazquez.askus.retrofit.interfaces

import es.iesnervion.avazquez.askus.DTOs.VotoModeracionDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface VotoModeracionInterface {
    @POST("/api/VotoModeracion")
    fun insertVotoModeracion(@Header("Authorization") authToken: String,
        @Body newVoto: VotoModeracionDTO): Call<Void>
}