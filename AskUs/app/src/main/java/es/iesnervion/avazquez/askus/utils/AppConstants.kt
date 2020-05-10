package es.iesnervion.avazquez.askus.utils

object AppConstants {
    const val BASE_URL = "https://proyectofinal-avazquez.azurewebsites.net/"
    const val UNAUTHORIZED = 401
    const val CONFLICT = 409
    const val OK = 200
    const val TOKEN_LENGHT = 3
    const val PREFERENCE_NAME = "AskUsPreferences"
    const val CACHE_SIZE = (5 * 1024 * 1024).toLong() //5 MB
    const val PASSWORD_MIN_LENGHT = 3   //just for testing, this should be at least 6 char
}