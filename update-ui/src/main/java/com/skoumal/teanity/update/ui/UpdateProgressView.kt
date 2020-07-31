package com.skoumal.teanity.update.ui

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.totalBytesToDownload
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UpdateProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    style: Int = 0
) : FrameLayout(context, attrs, style) {

    private val title: TextView by lazy { findViewById<TextView>(R.id.play_core_update_progress_title) }
    private val message: TextView by lazy { findViewById<TextView>(R.id.play_core_update_progress_message) }
    private val bar: ProgressBar by lazy { findViewById<ProgressBar>(R.id.play_core_update_progress_bar) }
    private val action: ImageView by lazy { findViewById<ImageView>(R.id.play_core_update_progress_action) }
    private val loading: ProgressBar by lazy { findViewById<ProgressBar>(R.id.play_core_update_progress_loading) }

    private var state = State.UNDEFINED
        set(value) {
            field = value
            moveToState(value)
        }

    private var megaBytesDownloaded: Long = 0
        set(value) {
            field = value
            val animator = bar.tag as? ValueAnimator
            val newAnimator = ValueAnimator.ofInt(
                animator?.animatedValue as? Int ?: bar.progress,
                value.toInt()
            ).also {
                it.interpolator = FastOutSlowInInterpolator()
                it.addUpdateListener {
                    bar.progress = it.animatedValue as Int
                }
            }

            if (animator?.isRunning == true) {
                animator.cancel()
            }
            bar.tag = newAnimator.also {
                it.start()
            }
        }
    private var megaBytesToDownload: Long = 0
        set(value) {
            field = value
            bar.max = value.toInt()
        }

    private var pendingClickListener: OnClickListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_update_progress, this, true)
        moveToState(state)
    }

    fun setAppUpdateResult(result: AppUpdateResult) {
        state = result.toState()
        when (result) {
            AppUpdateResult.NotAvailable -> isVisible = false

            is AppUpdateResult.InProgress -> setAppUpdateResult(result)
            is AppUpdateResult.Downloaded -> setAppUpdateResult(result)
            is AppUpdateResult.Available -> setAppUpdateResult(result).also {
                isVisible = true
            }
        }
    }

    private fun setAppUpdateResult(result: AppUpdateResult.InProgress) {
        megaBytesToDownload = result.installState.totalBytesToDownload
        megaBytesDownloaded = result.installState.bytesDownloaded

        message.text = context.getString(
            R.string.play_core_ui_downloading_message,
            megaBytesDownloaded / 1_000_000f,
            megaBytesToDownload / 1_000_000f
        )
    }

    private fun setAppUpdateResult(result: AppUpdateResult.Downloaded) {
        if (pendingClickListener == null) {
            setOnClickListener {
                GlobalScope.launch {
                    result.completeUpdate()
                }
            }
        }
    }

    private fun setAppUpdateResult(result: AppUpdateResult.Available) {
        megaBytesToDownload = result.updateInfo.totalBytesToDownload
        megaBytesDownloaded = result.updateInfo.bytesDownloaded
    }

    // ---

    private fun moveToState(state: State) {
        when (state) {
            State.AVAILABLE -> {
                title.isVisible = true
                message.isVisible = true
                bar.isVisible = false
                action.isVisible = false
                loading.isVisible = false

                title.setText(R.string.play_core_ui_available_title)
                message.setText(R.string.play_core_ui_available_message)
            }
            State.IN_PROGRESS -> {
                title.isVisible = true
                message.isVisible = true
                bar.isVisible = true
                action.isVisible = false
                loading.isVisible = false

                title.setText(R.string.play_core_ui_downloading_title)
            }
            State.DOWNLOADED -> {
                title.isVisible = true
                message.isVisible = true
                bar.isVisible = false
                action.isVisible = true
                loading.isVisible = false

                title.setText(R.string.play_core_ui_downloaded_title)
                message.setText(R.string.play_core_ui_downloaded_message)
            }
            State.UNDEFINED -> {
                title.isVisible = false
                message.isVisible = false
                bar.isVisible = false
                action.isVisible = false
                loading.isVisible = true

                title.text = null
                message.text = null
            }
        }

        setOnClickListener(pendingClickListener)
    }

    // ---

    override fun setOnClickListener(l: OnClickListener?) {
        if (state == State.DOWNLOADED) {
            super.setOnClickListener(l)
        } else if (pendingClickListener !== l) {
            pendingClickListener = l
        }
    }

    override fun setVisibility(visibility: Int) {
        val parent = parent
        if (parent is UpdateProgressTooltipLayout) {
            parent.visibility = visibility
            super.setVisibility(View.VISIBLE)
        } else {
            super.setVisibility(visibility)
        }
    }

    // ---

    private enum class State {
        UNDEFINED,
        IN_PROGRESS,
        DOWNLOADED,
        AVAILABLE
    }

    private fun AppUpdateResult.toState() = when (this) {
        AppUpdateResult.NotAvailable -> State.UNDEFINED
        is AppUpdateResult.Available -> State.AVAILABLE
        is AppUpdateResult.InProgress -> State.IN_PROGRESS
        is AppUpdateResult.Downloaded -> State.DOWNLOADED
    }

}

