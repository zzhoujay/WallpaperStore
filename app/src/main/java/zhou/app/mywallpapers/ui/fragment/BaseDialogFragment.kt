package zhou.app.mywallpapers.ui.fragment

import android.content.Context
import android.support.v4.app.DialogFragment
import zhou.app.mywallpapers.util.Notifier

/**
 * Created by zhou on 16-2-23.
 */
open class BaseDialogFragment : DialogFragment() {


    var notifier: Notifier? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Notifier) {
            notifier = context
        }
    }

    fun notice(n: Any? = null) {
        notifier?.notice(n)
    }
}