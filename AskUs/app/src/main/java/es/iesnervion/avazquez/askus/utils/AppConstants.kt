package es.iesnervion.avazquez.askus.utils

object AppConstants {
    const val BASE_URL = "https://proyectofinal-avazquez.azurewebsites.net/"
    const val UNAUTHORIZED = 401
    const val CONFLICT = 409
    const val OK = 200
    const val NO_CONTENT = 204
    const val INTERNAL_SERVER_ERROR = 500
    const val TOKEN_LENGHT = 3
    const val PREFERENCE_NAME = "AskUsPreferences"
    const val CACHE_SIZE = (5 * 1024 * 1024).toLong() //5 MB
    const val PASSWORD_MIN_LENGHT = 3   //just for testing, this should be at least 6 char
    const val EXTRA_PARAM_POST = "detail:_post"
    const val VIEW_NAME_TITLE_POST = "detail:_title"
    const val VIEW_NAME_BODY_POST = "detail:_body"
    const val VIEW_NAME_AUTHOR_POST = "detail:_author"
    const val VIEW_NAME_TAGS_POST = "detail:_tags"
    const val PROFILE_CURRENT_USER = "PROFILE_CURRENT_USER"
    const val PROFILE_ANOTHER_USER = "PROFILE_ANOTHER_USER"
    const val NEW_POST = "NEW_POST"
}