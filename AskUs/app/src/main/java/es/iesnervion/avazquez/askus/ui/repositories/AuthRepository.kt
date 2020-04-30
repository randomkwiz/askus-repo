package es.iesnervion.avazquez.askus.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.iesnervion.avazquez.askus.DTOs.PublicacionDTO
import es.iesnervion.avazquez.askus.models.Login
import es.iesnervion.avazquez.askus.models.User
import es.iesnervion.avazquez.askus.ui.usecase.CreateUserUseCase
import es.iesnervion.avazquez.askus.ui.usecase.LoadPostsUseCase
import es.iesnervion.avazquez.askus.ui.usecase.SendAuthUseCase
import javax.inject.Inject

class AuthRepository
@Inject
constructor() {
    internal var sendAuthUseCase: SendAuthUseCase
    internal var createUserUseCase: CreateUserUseCase
    private val loadingLiveData = MutableLiveData<Boolean>()
    private val showFinishMessage = MutableLiveData<Boolean>()
    private val token = MutableLiveData<List<Char>>()
    private val userCreated = MutableLiveData<List<Char>>()
    val finishMessage: LiveData<Boolean>
        get() = showFinishMessage

    init {
        sendAuthUseCase = SendAuthUseCase()
        createUserUseCase = CreateUserUseCase()
    }

    fun getLoadingLiveData(): LiveData<Boolean> {
        return loadingLiveData
    }

    fun useCaseCreateUser(user: User){
        createUserUseCase.createUser(object : RepositoryInterface{
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T> onSuccess(data: List<T>) {
                userCreated.postValue(data as List<Char>)
            }
        }, user)
    }


    fun useCaseCheckLogin(login: Login) {
        sendAuthUseCase.getTokenLogin(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T> onSuccess(data: List<T>) {
                token.postValue(data as List<Char>)
            }
        }, login)
    }

    fun getToken(): LiveData<List<Char>> {
        return token
    }
    fun getUserCreatedInfo(): LiveData<List<Char>> {
        return userCreated
    }
}