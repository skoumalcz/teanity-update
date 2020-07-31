package com.skoumal.teanity.update.ui.tool

import android.graphics.Path
import android.graphics.RectF

class PathCreator private constructor(
    private val definition: Builder.PathDefinition
) {

    class Builder(private val constraints: RectF) {

        constructor(width: Float, height: Float) : this(RectF(0f, 0f, width, height))

        private var padding: RectF = RectF()

        private var topLeftCorner: Float = 0f
        private var topRightCorner: Float = 0f
        private var bottomLeftCorner: Float = 0f
        private var bottomRightCorner: Float = 0f

        private var arrowSize: RectF = RectF()

        fun setPadding(rectf: RectF) = apply { padding = rectf }

        fun setTopLeftCornerRadius(radius: Float) = apply { topLeftCorner = radius }
        fun setTopRightCornerRadius(radius: Float) = apply { topRightCorner = radius }
        fun setBottomLeftCornerRadius(radius: Float) = apply { bottomLeftCorner = radius }
        fun setBottomRightCornerRadius(radius: Float) = apply { bottomRightCorner = radius }

        fun setArrowSize(width: Float, height: Float) =
            apply { arrowSize = RectF(0f, 0f, width, height) }

        fun create() = PathDefinition(
            FloatArray(4).apply {
                topLeft = topLeftCorner
                topRight = topRightCorner
                bottomLeft = bottomLeftCorner
                bottomRight = bottomRightCorner
            },
            RectF(
                constraints.left + padding.left,
                constraints.top + padding.top,
                constraints.right - padding.right,
                constraints.bottom - padding.bottom
            ),
            arrowSize // todo adjust its location
        ).create()

        class PathDefinition(
            val corners: FloatArray,
            val container: RectF,
            val arrow: RectF
        )

    }

    private val arrowLocation = RectF(
        definition.container.centerX() - definition.arrow.centerX(),
        definition.container.bottom - definition.arrow.height(),
        definition.container.centerX() + definition.arrow.centerX(),
        definition.container.bottom
    )

    private val bottomLeftCorner
        inline get() = RectF(
            definition.container.left,
            arrowLocation.top - definition.corners.bottomLeft,
            definition.container.left + definition.corners.bottomLeft,
            arrowLocation.top
        )

    private val topLeftCorner
        inline get() = RectF(
            definition.container.left,
            definition.container.top,
            definition.container.left + definition.corners.topLeft,
            definition.container.top + definition.corners.topLeft
        )

    private val topRightCorner
        inline get() = RectF(
            definition.container.right - definition.corners.topRight,
            definition.container.top,
            definition.container.right,
            definition.container.top + definition.corners.topRight
        )

    private val bottomRightCorner
        inline get() = RectF(
            definition.container.right - definition.corners.bottomRight,
            arrowLocation.top - definition.corners.bottomRight,
            definition.container.right,
            arrowLocation.top
        )

    fun buildInto(path: Path) {
        path.arcTo(bottomLeftCorner, 90f, 90f)
        path.arcTo(topLeftCorner, 180f, 90f)
        path.arcTo(topRightCorner, 270f, 90f)
        path.arcTo(bottomRightCorner, 0f, 90f)
        path.teardropIn(arrowLocation)
        path.close()
    }

    private fun Path.teardropIn(container: RectF) {
        val curveWidth = container.width() / 2f
        val curveHeight = container.height() / 2f
        lineTo(container.right + curveWidth / 2f, container.top)
        rCubicTo(
            -curveWidth / 2f, 0f,
            -curveWidth / 2f, 0f,
            -curveWidth, curveHeight
        )
        rCubicTo(
            -curveWidth / 2f, curveHeight,
            -curveWidth / 2f, curveHeight,
            -curveWidth, 0f
        )
        rCubicTo(
            -curveWidth / 2f, -curveHeight,
            -curveWidth / 2f, -curveHeight,
            -curveWidth, -curveHeight
        )
    }

    companion object {

        private fun Builder.PathDefinition.create() = PathCreator(this)

    }

}
