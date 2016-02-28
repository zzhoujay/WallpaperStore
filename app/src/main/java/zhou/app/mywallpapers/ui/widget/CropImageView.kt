package zhou.app.mywallpapers.ui.widget

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import zhou.app.mywallpapers.util.Callback
import zhou.app.mywallpapers.util.Callback0

/**
 * Created by zhou on 16-2-25.
 */
class CropImageView : ImageView {


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    val cropMatrix: Matrix by lazy {
        Matrix()
    }

    var offset = 0f
    var border = 0f
    var horizontal = true
    var hasMeasured = false
    var onTapCallback: Callback0? = null


    fun init() {
        super.setScaleType(ScaleType.MATRIX)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        resetMatrix()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        resetMatrix()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        resetMatrix()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        hasMeasured = true
        resetMatrix()
    }

    fun resetMatrix() {
        if (drawable == null || !hasMeasured) {
            return
        }
        val dwidth = drawable?.intrinsicWidth ?: 1
        val dheight = drawable?.intrinsicHeight ?: 1
        val vheight = height - paddingTop - paddingBottom
        val vwidth = width - paddingLeft - paddingRight
        val scale: Float
        var dx = 0f
        var dy = 0f

        if (dwidth * vheight > vwidth * dheight) {
            scale = vheight.toFloat() / dheight.toFloat()
            dx = (vwidth - dwidth * scale) * 0.5f
            horizontal = true
            border = ((dwidth * scale - vwidth) / 2)
        } else {
            scale = vwidth.toFloat() / dwidth.toFloat()
            dy = (vheight - dheight * scale) * 0.5f
            horizontal = false
            border = ((dheight * scale - vheight) / 2)
        }

        offset = 0f

        cropMatrix.setScale(scale, scale)
        cropMatrix.postTranslate(dx, dy)

        imageMatrix = cropMatrix

        invalidate()
    }


    val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            var d = if (horizontal) distanceX else distanceY
            var newMatrix = Matrix(cropMatrix)
            var reduce = d < 0
            var temp = d + offset

            if (reduce && (offset <= -border || temp <= -border)) {
                offset = -border
            } else if (!reduce && (offset >= border || temp >= border)) {
                offset = border
            } else {
                offset = temp
            }

            if (horizontal) {
                newMatrix.postTranslate(-offset, 0f)
            } else {
                newMatrix.postTranslate(0f, -offset)
            }
            imageMatrix = newMatrix
            invalidate()
            return true
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            if (onTapCallback != null) {
                onTapCallback!!.call()
                return true
            }
            return false
        }

    })

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

}
