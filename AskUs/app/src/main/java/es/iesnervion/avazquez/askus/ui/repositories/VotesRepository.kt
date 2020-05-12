package es.iesnervion.avazquez.askus.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.iesnervion.avazquez.askus.DTOs.VotoPublicacionDTO
import es.iesnervion.avazquez.askus.ui.usecase.CreateVotesUseCase
import javax.inject.Inject

class VotesRepository @Inject
constructor() {
    internal var createVotesUseCase: CreateVotesUseCase
    private val loadingLiveData = MutableLiveData<Boolean>()
    private val showFinishMessage = MutableLiveData<Boolean>()
    private val responseCode = MutableLiveData<Int>()
    val finishMessage: LiveData<Boolean>
        get() = showFinishMessage

    init {
        createVotesUseCase = CreateVotesUseCase()
    }

    fun getLoadingLiveData(): LiveData<Boolean> {
        return loadingLiveData
    }

    fun useCaseInsertVotoPublicacion(votoPublicacion: VotoPublicacionDTO, token: String) {
        createVotesUseCase.insertVotoPublicacion(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                //responseCode.postValue((data as List<Int>).firstOrNull())
                setResponseCode((data as List<Int>).firstOrNull())
            }
        }, voto = votoPublicacion, token = token)
    }

    fun setResponseCode(value: Int?) {
        responseCode.postValue(value)
    }

    fun getResponseCodeVotoPublicacionSent(): LiveData<Int> {
        return responseCode
    }
}