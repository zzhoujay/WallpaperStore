package zhou.app.mywallpapers.util

import android.content.Context
import org.jetbrains.anko.defaultSharedPreferences

/**
 * Created by zhou on 16-2-28.
 */

fun getBoolean(ctx: Context, key: String, def: Boolean = false): Boolean {
    return ctx.defaultSharedPreferences.getBoolean(key, def)
}

fun setBoolean(ctx: Context, key: String, value: Boolean) {
    ctx.defaultSharedPreferences.edit().putBoolean(key, value).commit()
}