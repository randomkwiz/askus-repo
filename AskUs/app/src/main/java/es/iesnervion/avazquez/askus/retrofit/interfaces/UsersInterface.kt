package es.iesnervion.avazquez.askus.retrofit.interfaces

import es.iesnervion.avazquez.askus.DTOs.ProfileDTO
import es.iesnervion.avazquez.askus.DTOs.UserDTO
import retrofit2.Call
import retrofit2.http.*

interface UsersInterface {
    @GET("/api/Users")
    fun getIDUserByNickname(@Header("Authorization") authToken: String,
        @Query("nickname") nickname: String): Call<Int>

    @GET("/api/Users/{id}?type=userprofile")
    fun getUserProfile(@Path("id") idUser: Int): Call<ProfileDTO>

    @GET("/api/Users/{id}")
    fun getFullUser(@Header("Authorization") authToken: String,
        @Path("id") idUser: Int): Call<UserDTO>

    @PUT("api/Users/{id}?type=makeUserAModerator")
    fun makeUserAModerator(@Header("Authorization") authToken: String,
        @Path("id") idUser: Int): Call<Void>

    @PUT("api/Users/{id}?type=changePassword")
    fun changePassword(@Header("Authorization") authToken: String,
        @Path("id") idUser: Int,
        @Query("newPassword") newPassword: String): Call<Void>
}