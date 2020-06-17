package es.iesnervion.avazquez.askus.ui.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import es.iesnervion.avazquez.askus.models.Login
import es.iesnervion.avazquez.askus.models.User
import es.iesnervion.avazquez.askus.ui.repositories.AuthRepository
import es.iesnervion.avazquez.askus.ui.repositories.UsersRepository
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import javax.inject.Inject

class AuthViewModel : ViewModel() {
    var login: Login
    lateinit var newUser: User

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var usersRepository: UsersRepository

    init {
        GlobalApplication.applicationComponent?.inject(this)
        login = Login("", "")
    }

    fun checkLogin() {
        authRepository.useCaseCheckLogin(login)
    }

    fun loadingLiveData(): LiveData<Boolean> {
        return authRepository.getLoadingLiveData()
    }

    fun errorLiveData(): LiveData<Boolean> {
        return authRepository.finishMessage
    }

    fun getToken(): LiveData<List<Char>> {
        return authRepository.getToken()
    }

    fun loadUserIDByNickname(nickname: String, token: String) {
        return usersRepository.useCaseLoadUserIDByNickname(nickname = nickname, token = token)
    }

    fun getIDUserByNickname(): LiveData<List<Int>> {
        return usersRepository.getUserIDByNickname()
    }

    fun getUserCreatedInfo(): LiveData<List<Char>> {
        return authRepository.getUserCreatedInfo()
    }

    fun createUser() {
        authRepository.useCaseCreateUser(newUser)
    }
}