package es.iesnervion.avazquez.askus.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.iesnervion.avazquez.askus.DTOs.ProfileDTO
import es.iesnervion.avazquez.askus.DTOs.UserDTO
import es.iesnervion.avazquez.askus.ui.usecase.DeleteUserUseCase
import es.iesnervion.avazquez.askus.ui.usecase.LoadUsersUseCase
import es.iesnervion.avazquez.askus.ui.usecase.UpdateUserUseCase
import javax.inject.Inject

class UsersRepository @Inject constructor() {
    internal var loadJSONUseCase: LoadUsersUseCase
    internal var updateUseCase: UpdateUserUseCase
    internal var deleteUseCase: DeleteUserUseCase
    private val loadingLiveData = MutableLiveData<Boolean>()
    private val showFinishMessage = MutableLiveData<Boolean>()
    private val userIDByNickname = MutableLiveData<List<Int>>()
    private val userProfile = MutableLiveData<ProfileDTO>()
    private val userDto = MutableLiveData<UserDTO>()
    private val responseCodeChangePassword = MutableLiveData<Int>()
    private val responseCodeMakeUserAModerator = MutableLiveData<Int>()
    private val responseCodeDeleteUser = MutableLiveData<Int>()
    val finishMessage: LiveData<Boolean>
        get() = showFinishMessage

    init {
        loadJSONUseCase = LoadUsersUseCase()
        updateUseCase = UpdateUserUseCase()
        deleteUseCase = DeleteUserUseCase()
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

            override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                userIDByNickname.postValue(data as List<Int>)
            }
        }, token, nickname)
    }

    fun useCaseLoadFullUser(token: String, id: Int) {
        loadJSONUseCase.getFullUser(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                if (moreInfo as Int == 200) {
                    userDto.postValue((data as List<UserDTO>).firstOrNull())
                }
            }
        }, token = token, idUser = id)
    }

    fun useCaseLoadUserProfile(idUser: Int) {
        loadJSONUseCase.getUserProfile(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                if (moreInfo as Int == 200) {
                    userProfile.postValue(data.last() as ProfileDTO)
                } else {
                    showFinishMessage.postValue(true)
                }
            }
        }, idUser = idUser)
    }

    fun useCaseChangePassword(token: String, idUser: Int, newPassword: String) {
        updateUseCase.changePassword(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                responseCodeChangePassword.postValue(moreInfo as Int)
            }
        }, token = token, idUser = idUser, newPassword = newPassword)
    }

    fun useCaseMakeUserAModerator(token: String, idUser: Int) {
        updateUseCase.makeUserAModerator(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                responseCodeMakeUserAModerator.postValue(moreInfo as Int)
            }
        }, token = token, idUser = idUser)
    }

    fun useCaseDeleteUser(token: String, idUser: Int) {
        deleteUseCase.deleteUser(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                responseCodeDeleteUser.postValue(moreInfo as Int)
            }
        }, token = token, idUser = idUser)
    }

    fun getUserIDByNickname(): LiveData<List<Int>> = userIDByNickname
    fun getUserProfile(): LiveData<ProfileDTO> = userProfile
    fun getFullUser(): LiveData<UserDTO> = userDto
    fun getResponseCodeMakeUserAModerator(): LiveData<Int> = responseCodeMakeUserAModerator
    fun getResponseCodeChangePassword(): LiveData<Int> = responseCodeChangePassword
    fun getResponseCodeDeleteUser(): LiveData<Int> = responseCodeDeleteUser
}