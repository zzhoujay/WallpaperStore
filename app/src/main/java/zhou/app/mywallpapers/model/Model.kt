package zhou.app.mywallpapers.model

import java.util.*

/**
 * Created by zhou on 16-2-20.
 */

data class Wallpaper(var title: String, var url: String, var date: Date = Date()) {
    var id: Int? = null

    constructor(id: Int, title: String, url: String, date: Date) : this(title, url, date) {
        this.id = id
    }
}