package es.iesnervion.avazquez.askus.retrofit.interfaces

import es.iesnervion.avazquez.askus.DTOs.ProfileDTO
import es.iesnervion.avazquez.askus.DTOs.UserDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface UsersInterface {
    @GET("/api/Users")
    fun getIDUserByNickname(@Header("Authorization") authToken: String,
        @Query("nickname") nickname: String): Call<Int>

    @GET("/api/Users/{id}?type=userprofile")
    fun getUserProfile(@Path("id") idUser: Int): Call<ProfileDTO>

    @GET("/api/Users/{id}")
    fun getFullUser(@Header("Authorization") authToken: String,
        @Path("id") idUser: Int): Call<UserDTO>
}