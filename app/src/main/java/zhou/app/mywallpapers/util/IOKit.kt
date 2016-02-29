package zhou.app.mywallpapers.util

import zhou.app.mywallpapers.App
import zhou.app.mywallpapers.common.Config
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * Created by zhou on 16-2-27.
 * IO工具
 */

fun loadWallpaperInputStream(path: String): InputStream? {
    if (path.startsWith(Config.Tag.asset_prefix)) {
        return App.instance.assets.open(path.replaceFirst(Config.Tag.asset_prefix, ""))
    }
    val file = File(path)
    if (file.exists()) {
        return FileInputStream(file)
    }
    return null
}