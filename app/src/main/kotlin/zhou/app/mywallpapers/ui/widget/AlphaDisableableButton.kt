/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

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
