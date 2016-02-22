package zhou.app.mywallpapers

import android.app.Application
import kotlin.properties.Delegates

/**
 * Created by zhou on 16-2-20.
 */
class App : Application() {

    companion object {
        var instance: App by Delegates.notNull<App>()
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}