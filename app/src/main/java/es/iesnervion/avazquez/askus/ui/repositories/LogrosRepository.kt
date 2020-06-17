package es.iesnervion.avazquez.askus.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.iesnervion.avazquez.askus.DTOs.LogroDTO
import es.iesnervion.avazquez.askus.ui.usecase.LoadLogrosUseCase
import javax.inject.Inject

class LogrosRepository
@Inject constructor() {
    internal var loadJSONUseCase: LoadLogrosUseCase
    private val loadingLiveData = MutableLiveData<Boolean>()
    private val showFinishMessage = MutableLiveData<Boolean>()
    private val allLogros = MutableLiveData<List<LogroDTO>>()
    private val logrosFromUser = MutableLiveData<List<LogroDTO>>()
    val finishMessage: LiveData<Boolean>
        get() = showFinishMessage

    init {
        loadJSONUseCase = LoadLogrosUseCase()
    }

    fun getLoadingLiveData(): LiveData<Boolean> {
        return loadingLiveData
    }

    fun useCaseLoadAllLogros() {
        loadJSONUseCase.getAllLogros(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                allLogros.postValue(data as List<LogroDTO>)
            }
        })
    }

    fun useCaseLoadLogrosFromUser(token: String, idUser: Int) {
        loadJSONUseCase.getLogrosDeUsuario(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                logrosFromUser.postValue(data as List<LogroDTO>)
            }
        }, token = token, idUsuario = idUser)
    }

    fun getAllLogros(): LiveData<List<LogroDTO>> {
        return allLogros
    }

    fun getLogrosFromUser(): LiveData<List<LogroDTO>> {
        return logrosFromUser
    }
}