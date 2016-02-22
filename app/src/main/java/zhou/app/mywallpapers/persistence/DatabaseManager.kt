package zhou.app.mywallpapers.persistence

import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*
import zhou.app.mywallpapers.App
import zhou.app.mywallpapers.model.Wallpaper
import java.util.*

/**
 * Created by zhou on 16-2-21.
 */

class DatabaseManager : ManagedSQLiteOpenHelper(App.instance, DatabaseConfig.DATABASE_NAME) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase?) {
        sqLiteDatabase?.createTable(DatabaseConfig.TABLE_NAME, true,
                DatabaseConfig.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                DatabaseConfig.TITLE to TEXT,
                DatabaseConfig.URL to TEXT,
                DatabaseConfig.DATE to INTEGER)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase?, p1: Int, p2: Int) {
        sqLiteDatabase?.dropTable(DatabaseConfig.TABLE_NAME, true)
        onCreate(sqLiteDatabase)
    }

    fun insert(wallpaper: Wallpaper) {
        use {
            insert(DatabaseConfig.TABLE_NAME,
                    DatabaseConfig.TITLE to wallpaper.title,
                    DatabaseConfig.URL to wallpaper.url,
                    DatabaseConfig.DATE to wallpaper.date.time)
        }
    }

    fun delete(id: Int) {
        use {
            delete(DatabaseConfig.TABLE_NAME, DatabaseConfig.ID, DatabaseConfig.ID to id)
        }
    }

    fun select(): ArrayList<Wallpaper> {
        var wallpapers: ArrayList<Wallpaper>? = null
        use {
            select(DatabaseConfig.TABLE_NAME).orderBy(DatabaseConfig.DATE, SqlOrderDirection.DESC).exec {
                if (count <= 0) {
                    return@exec
                }
                wallpapers = ArrayList(count)
                moveToFirst()
                if (id_index == null) {
                    id_index = getColumnIndex(DatabaseConfig.ID)
                    title_index = getColumnIndex(DatabaseConfig.TITLE)
                    url_index = getColumnIndex(DatabaseConfig.URL)
                    date_index = getColumnIndex(DatabaseConfig.DATE)
                }
                do {
                    val wallpaper = Wallpaper(getInt(id_index!!), getString(title_index!!), getString(url_index!!), Date(getLong(date_index!!)))
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

        val instance: DatabaseManager by lazy {
            DatabaseManager()
        }
    }

}

object DatabaseConfig {
    val DATABASE_NAME = "wallpapers.db"
    val TABLE_NAME = "wallpaper"
    val ID = "id"
    val TITLE = "title"
    val URL = "url"
    val DATE = "date"
}