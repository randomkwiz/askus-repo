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

    val finishMessage: LiveData<Boolean>
        get() = showFinishMessage

    init {
        loadJSONUseCase = LoadPostsUseCase()
    }

    fun getLoadingLiveData(): LiveData<Boolean> {
        return loadingLiveData
    }

    fun useCaseLoadJSON() {
        loadJSONUseCase.getNonDeletedPublicPostedPosts(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T> onSuccess(data: List<T>) {
                allNonDeletedPublicPostedPosts.value = (data as List<PublicacionDTO>)
            }
        })
    }

    fun getAllNonDeletedPublicPostedPosts(): LiveData<List<PublicacionDTO>> {
        return allNonDeletedPublicPostedPosts
    }

}