package es.iesnervion.avazquez.askus.retrofit.interfaces

import retrofit2.http.GET
import retrofit2.http.Query

interface LogrosInterface {
    @GET("/api/logros")
    fun getAllLogros()

    //Obtiene todos los logros de un usuario concreto
    @GET("/api/logros")
    fun getLogrosFromUser(@Query("idUsuario") idUsuario: Int)
}