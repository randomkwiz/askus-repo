package es.iesnervion.avazquez.askus.interfaces

interface HomeActivityCallback {
    fun onAddPostClicked(idTagUserWasSeeing: Int)
    fun onPostAdded(idTagUserWasSeeing: Int)
    fun onPostClicked(idPost: Int)
}