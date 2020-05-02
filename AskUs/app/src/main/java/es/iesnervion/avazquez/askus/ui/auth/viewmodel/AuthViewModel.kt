package es.iesnervion.avazquez.askus.ui.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import es.iesnervion.avazquez.askus.models.Login
import es.iesnervion.avazquez.askus.models.User
import es.iesnervion.avazquez.askus.ui.repositories.AuthRepository
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import javax.inject.Inject

class AuthViewModel : ViewModel() {
    lateinit var login: Login
    lateinit var newUser: User

    @Inject
    lateinit var authRepository: AuthRepository

    init {
        GlobalApplication.applicationComponent?.inject(this)
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

    fun getUserCreatedInfo(): LiveData<List<Char>> {
        return authRepository.getUserCreatedInfo()
    }

    fun createUser() {
        authRepository.useCaseCreateUser(newUser)
    }
}