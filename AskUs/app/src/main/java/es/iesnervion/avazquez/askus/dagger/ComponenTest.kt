package es.iesnervion.avazquez.askus.dagger

import dagger.Component
import es.iesnervion.avazquez.askus.dagger.modules.AppModule
import es.iesnervion.avazquez.askus.ui.auth.viewmodel.AuthViewModel
import es.iesnervion.avazquez.askus.ui.fragments.tabs.viewmodel.PostViewModel
import es.iesnervion.avazquez.askus.ui.repositories.AuthRepository
import es.iesnervion.avazquez.askus.ui.repositories.PostsRepository
import es.iesnervion.avazquez.askus.ui.repositories.UsersRepository
import es.iesnervion.avazquez.askus.ui.usecase.CreateUserUseCase
import es.iesnervion.avazquez.askus.ui.usecase.LoadPostsUseCase
import es.iesnervion.avazquez.askus.ui.usecase.LoadUsersUseCase
import es.iesnervion.avazquez.askus.ui.usecase.SendAuthUseCase
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface ComponenTest {
    fun inject(loadJSONUseCase: LoadPostsUseCase)
    fun inject(loadJSONUseCase: LoadUsersUseCase)
    fun inject(authViewModel: AuthViewModel)
    fun inject(sendAuthUseCase: SendAuthUseCase)
    fun inject(usersRepository: UsersRepository)
    fun inject(authRepository: AuthRepository)
    fun inject(postsRepository: PostsRepository)
    fun inject(createUserUseCase: CreateUserUseCase)
    fun inject(postViewModel: PostViewModel)
}