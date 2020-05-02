package es.iesnervion.avazquez.askus.ui.repositories

interface RepositoryInterface {
    fun showError(show: Boolean)
    fun onLoading(loading: Boolean)
    fun <T> onSuccess(data: List<T>)
}