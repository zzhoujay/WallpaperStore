package zhou.app.mywallpapers.common

/**
 * Created by zhou on 16-2-25.
 * 一些配置信息、常量等
 */

object Config {

    object Database {
        val DATABASE_NAME = "wallpapers.db"
        val TABLE_NAME = "wallpaper"
        val ID = "id"
        val TITLE = "title"
        val URL = "url"
        val DATE = "date"
        val PROTECT = "protect"
        val DATABASE_VERSION = 1
    }

    object Action {
        const val preview_wallpaper = 0x1234
        const val set_wallpaper = 0x1235
        const val cache_dialog = 0x432
        const val reload_list = 0x134
        const val notifier_permission_ready = 0x666
    }

    object Tag {
        const val wallpaper = "wallpaper"
        const val initStart = "initStart"
    }

    object Flag {
        const val result_select_image = 18
        const val result_pick_image = 0x123
        const val permission_flag = 5
    }

    object Id {
        const val menu_edit = 0x123
        const val menu_delete = 0x234
    }
}