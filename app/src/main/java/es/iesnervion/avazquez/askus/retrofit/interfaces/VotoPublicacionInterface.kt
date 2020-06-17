package es.iesnervion.avazquez.askus.retrofit.interfaces

import es.iesnervion.avazquez.askus.DTOs.VotoPublicacionDTO
import retrofit2.Call
import retrofit2.http.*

interface VotoPublicacionInterface {
    //Obtiene todos los votos de una publicación (votos normales, cuando la publicación ya está publicada).
    @GET("/api/votoPublicacion")
    fun getAllVotesFromPost(
        @Query("idPublicacion") idPublicacion: Int): Call<List<VotoPublicacionDTO>>

    //Obtiene todos los votos de una publicación con una valoración concreta
    @GET("/api/votoPublicacion")
    fun getAllConcretedVotesFromPost(@Query("idPublicacion") idPublicacion: Int,
        @Query("valoracion") valoracion: Boolean): Call<List<VotoPublicacionDTO>>

    @POST("/api/votopublicacion")
    fun insertVotoPublicacion(@Header("Authorization") authToken: String,
        @Body newVoto: VotoPublicacionDTO): Call<Void>
}