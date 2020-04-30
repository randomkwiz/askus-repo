package es.iesnervion.avazquez.askus.ui.usecase

import es.iesnervion.avazquez.askus.retrofit.interfaces.UsersInterface
import es.iesnervion.avazquez.askus.ui.repositories.RepositoryInterface
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class LoadUsersUseCase {
    @Inject
    lateinit var requestInterface: UsersInterface

    init {
        GlobalApplication.applicationComponent?.inject(this)
    }
}