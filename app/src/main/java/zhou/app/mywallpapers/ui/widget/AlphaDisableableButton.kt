package zhou.app.mywallpapers.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button

/**
 * A Button which becomes translucent when it is disabled
 */
class AlphaDisableableButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Button(context, attrs, defStyleAttr) {

    init {
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (enabled) {
            alpha = 1.0f
        } else {
            alpha = DISABLED_ALPHA_VALUE
        }
    }

    companion object {
        var DISABLED_ALPHA_VALUE = 0.4f
    }
}
