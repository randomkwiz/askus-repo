package es.iesnervion.avazquez.askus.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.DTOs.PublicacionDTO
import es.iesnervion.avazquez.askus.ui.usecase.LoadPostsUseCase
import es.iesnervion.avazquez.askus.ui.usecase.SendNewPostUseCase
import javax.inject.Inject

class PostsRepository
@Inject
constructor() {
    internal var loadJSONUseCase: LoadPostsUseCase
    internal var sendPostJSONUseCase: SendNewPostUseCase
    private val loadingLiveData = MutableLiveData<Boolean>()
    private val showFinishMessage = MutableLiveData<Boolean>()
    private val allNonDeletedPublicPostedPosts = MutableLiveData<List<PublicacionDTO>>()
    private val allVisiblePostsByGivenTag = MutableLiveData<List<PostCompletoParaMostrarDTO>>()
    private val allNonDeletedPostedPosts = MutableLiveData<List<PostCompletoParaMostrarDTO>>()
    private val responseCode = MutableLiveData<Int>()

    val finishMessage: LiveData<Boolean>
        get() = showFinishMessage

    init {
        loadJSONUseCase = LoadPostsUseCase()
        sendPostJSONUseCase = SendNewPostUseCase()
    }

    fun getLoadingLiveData(): LiveData<Boolean> {
        return loadingLiveData
    }

    fun useCaseLoadNonDeletedPostedPosts(token : String) {
        loadJSONUseCase.getListadoPostsCompletosParaMostrarCantidadComentarios(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T> onSuccess(data: List<T>) {
                allNonDeletedPostedPosts.postValue(data as List<PostCompletoParaMostrarDTO>)
                allVisiblePostsByGivenTag.postValue(data as List<PostCompletoParaMostrarDTO>)
            }
        }, token)
    }

    fun useCaseLoadNonDeletedPostedPostsByTag(token : String, idTag : Int) {
        loadJSONUseCase.getListadoPostsCompletosParaMostrarCantidadComentariosTag(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T> onSuccess(data: List<T>) {
                allVisiblePostsByGivenTag.postValue(data as List<PostCompletoParaMostrarDTO>)
            }
        }, token, idTag)
    }

    fun useCaseSendNewPosts(post: PublicacionDTO, tagList: List<Int>) {
        sendPostJSONUseCase.postNewPost(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T> onSuccess(data: List<T>) {
                responseCode.postValue((data as List<Int>).firstOrNull())

            }
        }, post, tagList)
    }



    fun getAllNonDeletedPublicPostedPosts(): LiveData<List<PublicacionDTO>> {
        return allNonDeletedPublicPostedPosts

    }fun getAllNonDeletedPostedPosts(): LiveData<List<PostCompletoParaMostrarDTO>> {
        return allNonDeletedPostedPosts
    }

    fun getAllVisiblePostsByGivenTag():LiveData<List<PostCompletoParaMostrarDTO>> {
        return allVisiblePostsByGivenTag
    }

    fun getResponseCodePostSent(): LiveData<Int> {
        return responseCode
    }
}