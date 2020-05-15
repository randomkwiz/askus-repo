package es.iesnervion.avazquez.askus.retrofit.interfaces

import es.iesnervion.avazquez.askus.DTOs.LogroDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface LogrosInterface {
    @GET("/api/logros")
    fun getAllLogros(): Call<List<LogroDTO>>

    //Obtiene todos los logros de un usuario concreto
    @GET("/api/logros")
    fun getLogrosFromUser(@Header("Authorization") authToken: String,
        @Query("idUsuario") idUsuario: Int): Call<List<LogroDTO>>
}