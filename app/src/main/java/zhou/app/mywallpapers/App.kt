package zhou.app.mywallpapers

import android.app.Application
import com.karumi.dexter.Dexter
import com.squareup.otto.Bus
import zhou.app.mywallpapers.common.Config
import zhou.app.mywallpapers.model.Wallpaper
import zhou.app.mywallpapers.persistence.DatabaseManager
import zhou.app.mywallpapers.util.getBoolean
import zhou.app.mywallpapers.util.setBoolean
import kotlin.properties.Delegates

/**
 * Created by zhou on 16-2-20.
 * Application
 */
class App : Application() {

    companion object {
        var instance: App by Delegates.notNull<App>()
    }

    val bus: Bus = Bus()


    override fun onCreate() {
        super.onCreate()
        instance = this
        Dexter.initialize(this)
        if (getBoolean(this, Config.Tag.initStart, true)) {
            DatabaseManager.instance.insert(Wallpaper("", "file:///android_asset/primary_3.jpg", true))
            DatabaseManager.instance.insert(Wallpaper("", "file:///android_asset/primary_2.jpg", true))
            DatabaseManager.instance.insert(Wallpaper("放风筝的女孩", "file:///android_asset/primary_1.jpg", true))
        }
        setBoolean(this, Config.Tag.initStart, false)
    }
}