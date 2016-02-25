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

    val currMatrix = Matrix()


    fun init() {
        super.setScaleType(ScaleType.MATRIX)
    }

    override fun onDraw(canvas: Canvas?) {
        imageMatrix = Matrix(currMatrix)
        super.onDraw(canvas)
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

    fun resetMatrix() {
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
        } else {
            scale = vwidth.toFloat() / dwidth.toFloat()
            dy = (vheight - dheight * scale) * 0.5f
        }

        currMatrix.setScale(scale, scale)
        currMatrix.postTranslate(dx, dy)
    }


    val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {

            currMatrix.postTranslate(-distanceX, 0f)
            imageMatrix = Matrix(currMatrix)
            invalidate()
            return true
        }

    })

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

}
