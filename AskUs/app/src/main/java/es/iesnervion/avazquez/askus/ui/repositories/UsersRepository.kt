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

    val finishMessage: LiveData<Boolean>
        get() = showFinishMessage

    init {
        loadJSONUseCase = LoadUsersUseCase()
    }

    fun getLoadingLiveData(): LiveData<Boolean> {
        return loadingLiveData
    }


}