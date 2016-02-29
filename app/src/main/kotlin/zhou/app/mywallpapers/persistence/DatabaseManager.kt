package zhou.app.mywallpapers.persistence

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.jetbrains.anko.db.*
import zhou.app.mywallpapers.App
import zhou.app.mywallpapers.common.Config
import zhou.app.mywallpapers.model.Wallpaper
import java.util.*

/**
 * Created by zhou on 16-2-21.
 * 数据库操作工具类
 */

class DatabaseManager : ManagedSQLiteOpenHelper(App.instance, Config.Database.DATABASE_NAME, version = Config.Database.DATABASE_VERSION) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase?) {
        sqLiteDatabase?.createTable(Config.Database.TABLE_NAME, true,
                Config.Database.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                Config.Database.TITLE to TEXT,
                Config.Database.URL to TEXT,
                Config.Database.PROTECT to INTEGER,
                Config.Database.DATE to INTEGER)

    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase?, p1: Int, p2: Int) {
        sqLiteDatabase?.dropTable(Config.Database.TABLE_NAME, true)
        onCreate(sqLiteDatabase)
    }

    /**
     * 插入数据
     */
    fun insert(wallpaper: Wallpaper): Boolean {
        return try {
            use {
                insert(Config.Database.TABLE_NAME,
                        Config.Database.TITLE to wallpaper.title,
                        Config.Database.URL to wallpaper.url,
                        Config.Database.PROTECT to wallpaper.protect,
                        Config.Database.DATE to wallpaper.date.time)
            }
            true
        } catch(e: Exception) {
            Log.d(TAG, "insert")
            false
        }
    }

    /**
     * 删除
     */
    fun delete(id: Int?): Boolean {
        if (id == null) {
            return false
        }
        return try {
            use {
                delete(Config.Database.TABLE_NAME, "${Config.Database.ID}={${Config.Database.ID}}", Config.Database.ID to id)
            }
            true
        } catch(e: Exception) {
            Log.d(TAG, "delete", e)
            false
        }
    }

    /**
     * 更新Title和Url
     */
    fun update(wallpaper: Wallpaper) {
        use {
            update(Config.Database.TABLE_NAME,
                    Config.Database.TITLE to wallpaper.title,
                    Config.Database.URL to wallpaper.url).where("${Config.Database.ID}={${Config.Database.ID}}", Config.Database.ID to wallpaper.id!!).exec()
        }
    }

    /**
     * 查询所有的数据
     */
    fun select(): ArrayList<Wallpaper> {
        var wallpapers: ArrayList<Wallpaper>? = null
        use {
            select(Config.Database.TABLE_NAME).orderBy(Config.Database.DATE, SqlOrderDirection.DESC).exec {
                if (count <= 0) {
                    return@exec
                }
                wallpapers = ArrayList(count)
                moveToFirst()
                if (id_index == null) {
                    id_index = getColumnIndex(Config.Database.ID)
                    title_index = getColumnIndex(Config.Database.TITLE)
                    url_index = getColumnIndex(Config.Database.URL)
                    date_index = getColumnIndex(Config.Database.DATE)
                    protect_index = getColumnIndex(Config.Database.PROTECT)
                }
                do {
                    val wallpaper = Wallpaper(getInt(id_index!!), getString(title_index!!), getString(url_index!!), getInt(protect_index!!) != 0, Date(getLong(date_index!!)))
                    wallpapers!!.add(wallpaper)
                } while (moveToNext())
            }
        }
        return wallpapers ?: ArrayList<Wallpaper>(0)
    }

    companion object {
        var id_index: Int? = null
        var title_index: Int? = null
        var url_index: Int? = null
        var date_index: Int? = null
        var protect_index: Int? = null

        const val TAG = "DatabaseManager"

        val instance: DatabaseManager by lazy {
            DatabaseManager()
        }
    }

}
