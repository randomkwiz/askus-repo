package es.iesnervion.avazquez.askus.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.iesnervion.avazquez.askus.DTOs.PaginHeader
import es.iesnervion.avazquez.askus.DTOs.PostCompletoListadoComentariosDTO
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.DTOs.PublicacionDTO
import es.iesnervion.avazquez.askus.ui.usecase.LoadPostsUseCase
import es.iesnervion.avazquez.askus.ui.usecase.SendNewPostUseCase
import javax.inject.Inject

class PostsRepository
@Inject constructor() {
    internal var loadJSONUseCase: LoadPostsUseCase
    internal var sendPostJSONUseCase: SendNewPostUseCase
    private val loadingLiveData = MutableLiveData<Boolean>()
    private val showFinishMessage = MutableLiveData<Boolean>()

    //    private val allNonDeletedPublicPostedPosts = MutableLiveData<List<PublicacionDTO>>()
    private val allVisiblePostsByGivenTag = MutableLiveData<List<PostCompletoParaMostrarDTO>>()
    private val allVisiblePostsByGivenTagTopRated =
            MutableLiveData<List<PostCompletoParaMostrarDTO>>()
    private val allVisiblePostsByGivenTagTopCommented =
            MutableLiveData<List<PostCompletoParaMostrarDTO>>()
    private val allNonDeletedPostedPosts = MutableLiveData<List<PostCompletoParaMostrarDTO>>()
    private val allNonDeletedPostedPostsTopRated =
            MutableLiveData<List<PostCompletoParaMostrarDTO>>()
    private val allNonDeletedPostedPostsTopCommented =
            MutableLiveData<List<PostCompletoParaMostrarDTO>>()
    private val postWithComments = MutableLiveData<PostCompletoListadoComentariosDTO>()
    private val responseCode = MutableLiveData<Int>()
    private val paginHeader = MutableLiveData<PaginHeader>()
    private val commentPaginHeader = MutableLiveData<PaginHeader>()


    val finishMessage: LiveData<Boolean>
        get() = showFinishMessage

    init {
        loadJSONUseCase = LoadPostsUseCase()
        sendPostJSONUseCase = SendNewPostUseCase()
    }

    fun getLoadingLiveData(): LiveData<Boolean> {
        return loadingLiveData
    }

    fun useCaseLoadNonDeletedPostedPosts(token: String, pageSize: Int, pageNumber: Int) {
        loadJSONUseCase.getListadoPostsCompletosParaMostrarCantidadComentarios(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                allNonDeletedPostedPosts.postValue(data as List<PostCompletoParaMostrarDTO>)
                allVisiblePostsByGivenTag.postValue(data as List<PostCompletoParaMostrarDTO>)
                paginHeader.postValue(moreInfo as PaginHeader)
            }
        }, token, pageSize = pageSize, pageNumber = pageNumber)
    }

    fun useCaseLoadNonDeletedPostedPostsTopRated(token: String, pageSize: Int, pageNumber: Int) {
        loadJSONUseCase.getListadoPostsCompletosParaMostrarCantidadComentariosTopRated(
            object : RepositoryInterface {
                override fun showError(show: Boolean) {
                    showFinishMessage.postValue(show)
                }

                override fun onLoading(loading: Boolean) {
                    loadingLiveData.postValue(loading)
                }

                override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                    allNonDeletedPostedPostsTopRated.postValue(
                        data as List<PostCompletoParaMostrarDTO>)
                    allVisiblePostsByGivenTagTopRated.postValue(
                        data as List<PostCompletoParaMostrarDTO>)
                    paginHeader.postValue(moreInfo as PaginHeader)
                }
            }, token, pageSize = pageSize, pageNumber = pageNumber)
    }

    fun useCaseLoadNonDeletedPostedPostsTopCommented(token: String,
        pageSize: Int,
        pageNumber: Int) {
        loadJSONUseCase.getListadoPostsCompletosParaMostrarCantidadComentariosTopCommented(
            object : RepositoryInterface {
                override fun showError(show: Boolean) {
                    showFinishMessage.postValue(show)
                }

                override fun onLoading(loading: Boolean) {
                    loadingLiveData.postValue(loading)
                }

                override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                    allNonDeletedPostedPostsTopCommented.postValue(
                        data as List<PostCompletoParaMostrarDTO>)
                    allVisiblePostsByGivenTagTopCommented.postValue(
                        data as List<PostCompletoParaMostrarDTO>)
                    paginHeader.postValue(moreInfo as PaginHeader)
                }
            }, token, pageSize = pageSize, pageNumber = pageNumber)
    }

    fun useCaseLoadNonDeletedPostedPostsByTag(token: String,
        idTag: Int,
        pageNumber: Int,
        pageSize: Int) {
        loadJSONUseCase.getListadoPostsCompletosParaMostrarCantidadComentariosTag(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                allVisiblePostsByGivenTag.postValue(data as List<PostCompletoParaMostrarDTO>)
                paginHeader.postValue(moreInfo as PaginHeader)
            }
        }, token = token, idTag = idTag, pageNumber = pageNumber, pageSize = pageSize)
    }

    //tag - top rated
    fun useCaseLoadNonDeletedPostedPostsByTagTopRated(token: String,
        idTag: Int,
        pageNumber: Int,
        pageSize: Int) {
        loadJSONUseCase.getListadoPostsCompletosParaMostrarCantidadComentariosTagTopRated(
            object : RepositoryInterface {
                override fun showError(show: Boolean) {
                    showFinishMessage.postValue(show)
                }

                override fun onLoading(loading: Boolean) {
                    loadingLiveData.postValue(loading)
                }

                override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                    allVisiblePostsByGivenTagTopRated.postValue(
                        data as List<PostCompletoParaMostrarDTO>)
                    paginHeader.postValue(moreInfo as PaginHeader)
                }
            }, token = token, idTag = idTag, pageNumber = pageNumber, pageSize = pageSize)
    }

    //tag - top commented
    fun useCaseLoadNonDeletedPostedPostsByTagTopCommented(token: String,
        idTag: Int,
        pageNumber: Int,
        pageSize: Int) {
        loadJSONUseCase.getListadoPostsCompletosParaMostrarCantidadComentariosTagTopCommented(
            object : RepositoryInterface {
                override fun showError(show: Boolean) {
                    showFinishMessage.postValue(show)
                }

                override fun onLoading(loading: Boolean) {
                    loadingLiveData.postValue(loading)
                }

                override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                    allVisiblePostsByGivenTagTopCommented.postValue(
                        data as List<PostCompletoParaMostrarDTO>)
                    paginHeader.postValue(moreInfo as PaginHeader)
                }
            }, token = token, idTag = idTag, pageNumber = pageNumber, pageSize = pageSize)
    }

    fun useCaseLoadPostWithAllComments(token: String, idPost: Int, pageNumber: Int, pageSize: Int) {
        loadJSONUseCase.getPublicacionParaMostrarConComentarios(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                postWithComments.postValue((data as List<PostCompletoListadoComentariosDTO>).firstOrNull())
                commentPaginHeader.postValue(moreInfo as PaginHeader)
            }
        }, token = token, idPost = idPost, pageSize = pageSize, pageNumber = pageNumber)
    }

    fun useCaseSendNewPosts(post: PublicacionDTO, tagList: List<Int>) {
        sendPostJSONUseCase.postNewPost(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                responseCode.postValue((data as List<Int>).firstOrNull())

            }
        }, post, tagList)
    }
    /*Estas propiedades tienen getters porque devuelven LiveData y en la clase son MutableLiveData
    * Si no, no harían falta getters por la filosofía de Kotlin.
    * */
    //    fun getAllNonDeletedPublicPostedPosts(): LiveData<List<PublicacionDTO>> {
    //        return allNonDeletedPublicPostedPosts
    //
    //    }
    fun getAllNonDeletedPostedPosts(): LiveData<List<PostCompletoParaMostrarDTO>> {
        return allNonDeletedPostedPosts
    }

    fun getAllNonDeletedPostedPostsTopRated(): LiveData<List<PostCompletoParaMostrarDTO>> {
        return allNonDeletedPostedPostsTopRated
    }

    fun getAllNonDeletedPostedPostsTopCommented(): LiveData<List<PostCompletoParaMostrarDTO>> {
        return allNonDeletedPostedPostsTopCommented
    }

    fun getAllVisiblePostsByGivenTag():LiveData<List<PostCompletoParaMostrarDTO>> {
        return allVisiblePostsByGivenTag
    }

    fun getAllVisiblePostsByGivenTagTopRated(): LiveData<List<PostCompletoParaMostrarDTO>> {
        return allVisiblePostsByGivenTagTopRated
    }

    fun getAllVisiblePostsByGivenTagTopCommented(): LiveData<List<PostCompletoParaMostrarDTO>> {
        return allVisiblePostsByGivenTagTopCommented
    }

    fun getResponseCodePostSent(): LiveData<Int> {
        return responseCode
    }

    fun getPostWithAllComments(): LiveData<PostCompletoListadoComentariosDTO> {
        return postWithComments
    }

    fun getPaginHeaders(): LiveData<PaginHeader> {
        return paginHeader
    }

    fun getCommentsPaginHeaders(): LiveData<PaginHeader> {
        return commentPaginHeader
    }
}