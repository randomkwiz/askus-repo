package es.iesnervion.avazquez.askus.interfaces

import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO

interface HomeActivityCallback {
    fun onAddPostClicked(idTagUserWasSeeing: Int)
    fun onPostAdded(idTagUserWasSeeing: Int)
    fun onPostClicked(post: PostCompletoParaMostrarDTO)
    fun onUserClicked(idUser: Int, nickname: String, fromDetails: Boolean)
    fun logOut()
}