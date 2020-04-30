package es.iesnervion.avazquez.askus.dagger

import dagger.Component
import es.iesnervion.avazquez.askus.dagger.modules.AppModule
import es.iesnervion.avazquez.askus.ui.auth.viewmodel.AuthViewModel
import es.iesnervion.avazquez.askus.ui.usecase.LoadPostsUseCase
import es.iesnervion.avazquez.askus.ui.usecase.LoadUsersUseCase
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface ComponenTest {
    fun inject(loadJSONUseCase: LoadPostsUseCase)
    fun inject(loadJSONUseCase: LoadUsersUseCase)
    fun inject(authViewModel: AuthViewModel)
}