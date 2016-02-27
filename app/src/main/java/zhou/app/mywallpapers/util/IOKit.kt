package zhou.app.mywallpapers.util

import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * Created by zhou on 16-2-27.
 */

fun loadWallpaperInputStream(path: String): InputStream? {
    val file = File(path)
    if (file.exists()) {
        return FileInputStream(file)
    }
    return null
}