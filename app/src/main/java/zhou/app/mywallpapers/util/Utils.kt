package zhou.app.mywallpapers.util

import android.support.v4.app.Fragment
import android.widget.Toast
import zhou.app.mywallpapers.App

/**
 * Created by zhou on 16-2-23.
 */


fun Fragment.toast(msg: CharSequence, time: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, msg, time).show()
}

//fun Fragment.notice(notifierId: Any? = null) {
//    if (activity is Notifier) {
//        (activity as Notifier).notice(notifierId)
//    }
//}

class Event(val code: Int, val value: Any? = null, val from: Any? = null, val `to`: Any? = null)

fun  Any.notice(event: Event) {
    App.instance.bus.post(event)
}