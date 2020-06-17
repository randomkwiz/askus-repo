package es.iesnervion.avazquez.askus.ui.repositories

interface RepositoryInterface {
    fun showError(show: Boolean)
    fun onLoading(loading: Boolean)
    fun <T, I> onSuccess(data: List<T>, moreInfo: I?)
}