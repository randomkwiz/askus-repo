package es.iesnervion.avazquez.askus.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.iesnervion.avazquez.askus.DTOs.VotoModeracionDTO
import es.iesnervion.avazquez.askus.ui.usecase.CreateModerationVoteUseCase
import javax.inject.Inject

class ModerationVotesRepository @Inject constructor() {
    internal var createVotesUseCase: CreateModerationVoteUseCase
    private val loadingLiveData = MutableLiveData<Boolean>()
    private val showFinishMessage = MutableLiveData<Boolean>()
    private val responseCode = MutableLiveData<Int>()
    val finishMessage: LiveData<Boolean>
        get() = showFinishMessage

    init {
        createVotesUseCase = CreateModerationVoteUseCase()
    }

    fun getLoadingLiveData(): LiveData<Boolean> {
        return loadingLiveData
    }

    fun useCaseInsertVotoModeracion(votoModeracion: VotoModeracionDTO, token: String) {
        createVotesUseCase.insertVotoModeracion(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                setResponseCode(moreInfo as Int)
            }
        }, voto = votoModeracion, token = token)
    }

    fun setResponseCode(value: Int?) {
        responseCode.postValue(value)
    }

    fun getResponseCodeVotoModeracionSent(): LiveData<Int> {
        return responseCode
    }
}