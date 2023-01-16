package co.selim.browser

import android.app.Application
import co.selim.browser.api.apiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PhotoBrowserApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger()
            }
            androidContext(this@PhotoBrowserApplication)
            modules(apiModule)
        }
    }
}