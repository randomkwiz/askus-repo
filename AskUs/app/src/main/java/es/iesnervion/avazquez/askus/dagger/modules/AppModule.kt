package es.iesnervion.avazquez.askus.dagger.modules

import android.app.Application
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import es.iesnervion.avazquez.askus.retrofit.interfaces.*
import es.iesnervion.avazquez.askus.ui.repositories.AuthRepository
import es.iesnervion.avazquez.askus.ui.repositories.PostsRepository
import es.iesnervion.avazquez.askus.ui.repositories.UsersRepository
import es.iesnervion.avazquez.askus.utils.AppConstants.BASE_URL
import es.iesnervion.avazquez.askus.utils.AppConstants.CACHE_SIZE
import es.iesnervion.avazquez.askus.utils.UtilClass.Companion.hasNetwork
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule(private val mApplication: Application) {
    @Provides
    @Singleton
    fun provideApplication(): Application {
        return mApplication
    }

    @Provides
    @Singleton
    fun usersRepository(): UsersRepository {
        return UsersRepository()
    }

    @Provides
    @Singleton
    fun authRepository(): AuthRepository {
        return AuthRepository()
    }

    @Provides
    @Singleton
    fun postsRepository(): PostsRepository {
        return PostsRepository()
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

    @Provides
    @Singleton
    fun provideHttpCache(application: Application): Cache {
        return Cache(application.cacheDir, CACHE_SIZE)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(cache: Cache): OkHttpClient {
        return OkHttpClient.Builder()
            // Specify the cache we created earlier.
            .cache(cache)
            // Add an Interceptor to the OkHttpClient.
            .addInterceptor { chain ->
                // Get the request from the chain.
                var request = chain.request()
                /*
                *  Leveraging the advantage of using Kotlin,
                *  we initialize the request and change its header depending on whether
                *  the device is connected to Internet or not.
                */
                request = if (hasNetwork())
                /*
                *  If there is Internet, get the cache that was stored 5 seconds ago.
                *  If the cache is older than 5 seconds, then discard it,
                *  and indicate an error in fetching the response.
                *  The 'max-age' attribute is responsible for this behavior.
                */
                    request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                else
                /*
                *  If there is no Internet, get the cache that was stored 7 days ago.
                *  If the cache is older than 7 days, then discard it,
                *  and indicate an error in fetching the response.
                *  The 'max-stale' attribute is responsible for this behavior.
                *  The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
                */
                    request.newBuilder().header("Cache-Control",
                        "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
                // End of if-else statement
                // Add the modified request to the chain.
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
    }
}