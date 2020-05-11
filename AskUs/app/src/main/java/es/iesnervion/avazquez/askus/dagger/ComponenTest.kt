package es.iesnervion.avazquez.askus.dagger

import dagger.Component
import es.iesnervion.avazquez.askus.dagger.modules.AppModule
import es.iesnervion.avazquez.askus.ui.auth.viewmodel.AuthViewModel
import es.iesnervion.avazquez.askus.ui.details.viewmodel.DetailsViewModel
import es.iesnervion.avazquez.askus.ui.fragments.tabs.viewmodel.MainViewModel
import es.iesnervion.avazquez.askus.ui.repositories.*
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
    fun inject(votesRepository: VotesRepository)
    fun inject(authRepository: AuthRepository)
    fun inject(postsRepository: PostsRepository)
    fun inject(tagsRepository: TagsRepository)
    fun inject(createUserUseCase: CreateUserUseCase)
    fun inject(mainViewModel: MainViewModel)
    fun inject(loadTagsUseCase: LoadTagsUseCase)
    fun inject(sendNewPostUseCase: SendNewPostUseCase)
    fun inject(createVotesUseCase: CreateVotesUseCase)
    fun inject(detailsViewModel: DetailsViewModel)
    fun inject(createCommentUseCase: CreateCommentUseCase)
}