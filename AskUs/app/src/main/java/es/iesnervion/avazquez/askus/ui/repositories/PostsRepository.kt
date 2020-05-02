package es.iesnervion.avazquez.askus.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.iesnervion.avazquez.askus.DTOs.PublicacionDTO
import es.iesnervion.avazquez.askus.ui.usecase.LoadPostsUseCase
import javax.inject.Inject

class PostsRepository
@Inject
constructor() {
    internal var loadJSONUseCase: LoadPostsUseCase
    private val loadingLiveData = MutableLiveData<Boolean>()
    private val showFinishMessage = MutableLiveData<Boolean>()
    private val allNonDeletedPublicPostedPosts = MutableLiveData<List<PublicacionDTO>>()
    private val allNonDeletedPostedPosts = MutableLiveData<List<PublicacionDTO>>()

    val finishMessage: LiveData<Boolean>
        get() = showFinishMessage

    init {
        loadJSONUseCase = LoadPostsUseCase()
    }

    fun getLoadingLiveData(): LiveData<Boolean> {
        return loadingLiveData
    }

    fun useCaseLoadNonDeletedPublicPostedPosts() {
        loadJSONUseCase.getNonDeletedPublicPostedPosts(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T> onSuccess(data: List<T>) {
                allNonDeletedPublicPostedPosts.postValue(data as List<PublicacionDTO>)
            }
        })
    }

    fun useCaseLoadNonDeletedPostedPosts(token : String) {
        loadJSONUseCase.getNonDeletedPostedPosts(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T> onSuccess(data: List<T>) {
                allNonDeletedPostedPosts.postValue(data as List<PublicacionDTO>)
            }
        }, token)
    }

    fun getAllNonDeletedPublicPostedPosts(): LiveData<List<PublicacionDTO>> {
        return allNonDeletedPublicPostedPosts

    }fun getAllNonDeletedPostedPosts(): LiveData<List<PublicacionDTO>> {
        return allNonDeletedPostedPosts
    }

}