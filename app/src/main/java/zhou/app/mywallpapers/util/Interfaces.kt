package zhou.app.mywallpapers.util

import android.view.View

/**
 * Created by zhou on 16-2-21.
 * 一些接口
 */

interface OnClickCallback {
    fun onClick(view: View, position: Int)
}

interface Callback0{
    fun call()
}

interface Callback<T> {
    fun call(t: T? = null)
}

interface Callback2<T, K> {
    fun call(t: T? = null, k: K? = null)
}

interface Callback3<T, K, M> {
    fun call(t: T? = null, k: K? = null, m: M? = null)
}
