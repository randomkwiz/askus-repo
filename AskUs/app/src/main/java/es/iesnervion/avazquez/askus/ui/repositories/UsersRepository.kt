package es.iesnervion.avazquez.askus.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.iesnervion.avazquez.askus.DTOs.UserDTO
import es.iesnervion.avazquez.askus.ui.usecase.LoadUsersUseCase
import javax.inject.Inject

class UsersRepository @Inject
constructor() {
    internal var loadJSONUseCase: LoadUsersUseCase
    private val loadingLiveData = MutableLiveData<Boolean>()
    private val showFinishMessage = MutableLiveData<Boolean>()
    private val allUsers = MutableLiveData<List<UserDTO>>()
    private val userIDByNickname = MutableLiveData<List<Int>>()
    val finishMessage: LiveData<Boolean>
        get() = showFinishMessage

    init {
        loadJSONUseCase = LoadUsersUseCase()
    }

    fun getLoadingLiveData(): LiveData<Boolean> {
        return loadingLiveData
    }

    fun useCaseLoadUserIDByNickname(token: String, nickname: String) {
        loadJSONUseCase.getIDUserByNickname(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T> onSuccess(data: List<T>) {
                userIDByNickname.postValue(data as List<Int>)
            }
        }, token, nickname)
    }

    fun getUserIDByNickname(): LiveData<List<Int>> {
        return userIDByNickname
    }
}