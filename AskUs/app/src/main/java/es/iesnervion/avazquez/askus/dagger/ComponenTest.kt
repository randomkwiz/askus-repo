package es.iesnervion.avazquez.askus.dagger

import dagger.Component
import es.iesnervion.avazquez.askus.dagger.modules.AppModule
import es.iesnervion.avazquez.askus.ui.auth.viewmodel.AuthViewModel
import es.iesnervion.avazquez.askus.ui.fragments.tabs.viewmodel.MainViewModel
import es.iesnervion.avazquez.askus.ui.repositories.AuthRepository
import es.iesnervion.avazquez.askus.ui.repositories.PostsRepository
import es.iesnervion.avazquez.askus.ui.repositories.TagsRepository
import es.iesnervion.avazquez.askus.ui.repositories.UsersRepository
import es.iesnervion.avazquez.askus.ui.usecase.*
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
    fun inject(tagsRepository: TagsRepository)
    fun inject(createUserUseCase: CreateUserUseCase)
    fun inject(mainViewModel: MainViewModel)
    fun inject(loadTagsUseCase: LoadTagsUseCase)
}