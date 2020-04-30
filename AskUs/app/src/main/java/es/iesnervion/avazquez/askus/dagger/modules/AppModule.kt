package es.iesnervion.avazquez.askus.dagger.modules

import dagger.Module
import dagger.Provides
import es.iesnervion.avazquez.askus.retrofit.interfaces.*
import es.iesnervion.avazquez.askus.ui.repositories.UsersRepository
import es.iesnervion.avazquez.askus.utils.AppConstants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
@Module
class AppModule() {
    @Provides
    @Singleton
    fun usersRepository(): UsersRepository {
        return UsersRepository()
    }

    @Provides
    @Singleton
    fun provideRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstants.ENDPOINT)
            .client(OkHttpClient().newBuilder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    internal fun provideAuthInterface(retrofit: Retrofit): AuthInterface {
        return retrofit.create(AuthInterface::class.java)
    }

    @Provides
    internal fun provideComentarioInterface(retrofit: Retrofit): ComentariosInterface {
        return retrofit.create(ComentariosInterface::class.java)
    }

    @Provides
    internal fun provideLogrosInterface(retrofit: Retrofit): LogrosInterface {
        return retrofit.create(LogrosInterface::class.java)
    }
    @Provides
    internal fun providePublicacionesInterface(retrofit: Retrofit): PublicacionesInterface {
        return retrofit.create(PublicacionesInterface::class.java)
    }
    @Provides
    internal fun provideTagsInterface(retrofit: Retrofit): TagsInterface {
        return retrofit.create(TagsInterface::class.java)
    }
    @Provides
    internal fun provideUsersInterface(retrofit: Retrofit): UsersInterface {
        return retrofit.create(UsersInterface::class.java)
    }
    @Provides
    internal fun provideVotoModeracionInterface(retrofit: Retrofit): VotoModeracionInterface {
        return retrofit.create(VotoModeracionInterface::class.java)
    }
    @Provides
    internal fun provideVotoPublicacionInterface(retrofit: Retrofit): VotoPublicacionInterface {
        return retrofit.create(VotoPublicacionInterface::class.java)
    }
}