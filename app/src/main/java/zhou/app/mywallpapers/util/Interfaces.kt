package zhou.app.mywallpapers.util

import android.view.View

/**
 * Created by zhou on 16-2-21.
 */

interface OnClickCallback {
    fun onClick(view: View, position: Int)
}

interface Callback<T> {
    fun call(t: T)
}