package zhou.app.mywallpapers.ui.fragment

import android.content.Context
import android.support.v4.app.Fragment
import zhou.app.mywallpapers.util.Notifier

/**
 * Created by zhou on 16-2-22.
 */
open class BaseFragment : Fragment() {

    var notifier: Notifier? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Notifier) {
            notifier = context
        }
    }

    open fun notice(notifierId: Int) {
        notifier?.notice(notifierId)
    }

}