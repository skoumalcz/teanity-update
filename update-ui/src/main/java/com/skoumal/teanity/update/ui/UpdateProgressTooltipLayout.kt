package com.skoumal.teanity.update.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.core.content.res.use
import androidx.core.view.updatePadding
import com.skoumal.teanity.update.ui.tool.*
import kotlin.math.roundToInt

@SuppressLint("Recycle")
class UpdateProgressTooltipLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    style: Int = 0
) : PathClippedFrameLayout(context, attrs, style) {

    var arrowSize: RectF = RectF()
        set(value) {
            field = value
            clipPath.reset()
            postInvalidate()
            updatePaddingBottom()
        }
    var cornerRadii = floatArrayOf(0f, 0f, 0f, 0f)
        set(value) {
            require(value.size == 4)
            field = value
            clipPath.reset()
            postInvalidate()
        }

    private val padding
        inline get() = if (clipToPadding) RectF(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            paddingRight.toFloat(),
            paddingBottom.toFloat()
        ) else RectF()

    private var internalPaddingBottom: Float = paddingBottom.toFloat()
        set(value) {
            field = value
            updatePaddingBottom()
        }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.UpdateProgressTooltipLayout).use {
            arrowSize = it.arrowSize
            cornerRadii = it.cornerRadii
            internalPaddingBottom = it.paddingBottom
        }
    }

    override fun onCreatePath(path: Path) {
        PathCreator.Builder(measuredWidth.toFloat(), measuredHeight.toFloat())
            .setPadding(padding)
            .setArrowSize(arrowSize.width(), arrowSize.height())
            .setTopLeftCornerRadius(cornerRadii.topLeft)
            .setTopRightCornerRadius(cornerRadii.topRight)
            .setBottomLeftCornerRadius(cornerRadii.bottomLeft)
            .setBottomRightCornerRadius(cornerRadii.bottomRight)
            .create()
            .buildInto(path)
    }

    fun setCornerRadius(radius: Float) {
        cornerRadii = floatArrayOf(radius, radius, radius, radius)
    }

    private fun updatePaddingBottom() {
        if (!clipToPadding) {
            updatePadding(bottom = (arrowSize.height() + internalPaddingBottom).roundToInt())
        }
    }

    // ---

    private val TypedArray.arrowSize
        inline get() = RectF(
            0f, 0f,
            getDimension(
                R.styleable.UpdateProgressTooltipLayout_arrowWidth,
                this@UpdateProgressTooltipLayout.arrowSize.width()
            ),
            getDimension(
                R.styleable.UpdateProgressTooltipLayout_arrowHeight,
                this@UpdateProgressTooltipLayout.arrowSize.height()
            )
        )

    private val TypedArray.cornerRadii
        inline get() = this@UpdateProgressTooltipLayout.cornerRadii.copyOf().also {
            it.topLeft = getDimension(
                R.styleable.UpdateProgressTooltipLayout_cornerSizeTopLeft,
                it.topLeft
            )
            it.topRight = getDimension(
                R.styleable.UpdateProgressTooltipLayout_cornerSizeTopRight,
                it.topRight
            )
            it.bottomLeft = getDimension(
                R.styleable.UpdateProgressTooltipLayout_cornerSizeBottomLeft,
                it.bottomLeft
            )
            it.bottomRight = getDimension(
                R.styleable.UpdateProgressTooltipLayout_cornerSizeBottomRight,
                it.bottomRight
            )
        }

    private val TypedArray.paddingBottom
        inline get() = getDimension(
            R.styleable.UpdateProgressTooltipLayout_android_paddingBottom,
            internalPaddingBottom
        )

}