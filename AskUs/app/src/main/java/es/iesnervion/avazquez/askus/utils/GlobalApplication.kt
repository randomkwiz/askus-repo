package es.iesnervion.avazquez.askus.utils

import android.app.Application
import es.iesnervion.avazquez.askus.dagger.ComponenTest
import es.iesnervion.avazquez.askus.dagger.DaggerComponenTest
import es.iesnervion.avazquez.askus.dagger.modules.AppModule

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        application = this
        applicationComponent =
            DaggerComponenTest.builder().appModule(AppModule()).build()
    }

    companion object {
        var application: Application? = null
            private set
        var applicationComponent: ComponenTest? = null
            private set
    }
}