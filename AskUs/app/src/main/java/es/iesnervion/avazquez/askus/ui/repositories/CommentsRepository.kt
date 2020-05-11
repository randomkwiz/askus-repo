package es.iesnervion.avazquez.askus.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.iesnervion.avazquez.askus.DTOs.ComentarioDTO
import es.iesnervion.avazquez.askus.ui.usecase.CreateCommentUseCase
import javax.inject.Inject

class CommentsRepository
@Inject
constructor() {
    internal var createCommentUseCase: CreateCommentUseCase
    private val loadingLiveData = MutableLiveData<Boolean>()
    private val showFinishMessage = MutableLiveData<Boolean>()
    private val responseCode = MutableLiveData<Int>()
    val finishMessage: LiveData<Boolean>
        get() = showFinishMessage

    init {
        createCommentUseCase = CreateCommentUseCase()
    }

    fun getLoadingLiveData(): LiveData<Boolean> {
        return loadingLiveData
    }

    fun useCaseInsertComment(comment: ComentarioDTO) {
        createCommentUseCase.insertComentario(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T> onSuccess(data: List<T>) {
                responseCode.postValue(((data as List<Int>).firstOrNull()))
            }
        }, comment)
    }

    fun getResponseCodeCommentSent(): LiveData<Int> {
        return responseCode
    }
}