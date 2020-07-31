package com.skoumal.teanity.update.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.model.InstallErrorCode
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.AppUpdateResult
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlin.random.Random.Default.nextInt

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContainersVisible(false)
        progress_action.setOnClickListener {
            downloadUpdate()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun downloadUpdate() {
        setContainersVisible(true)

        val manager = AppUpdateManagerFactory.create(applicationContext)
        val channel = GlobalScope.produce {
            delay(2000)

            send(AppUpdateResult.Available(manager, appUpdateInfo))

            delay(2000)

            val max = 10000000L
            var progress = 0L
            while (progress < max) {
                send(AppUpdateResult.InProgress(installState(progress, max)))
                delay(500)
                progress += nextInt(1_000_000)
            }

            send(AppUpdateResult.Downloaded(manager))
        }

        GlobalScope.launch {
            channel.consumeEach {
                withContext(Dispatchers.Main.immediate) {
                    progress_view.setAppUpdateResult(it)
                    progress_view_custom.setAppUpdateResult(it)
                    progress_view_custom_toolbar.setAppUpdateResult(it)
                }
            }
        }
    }

    private fun setContainersVisible(isVisible: Boolean) {
        progress_container.isVisible = isVisible
        progress_view_custom_toolbar.isVisible = isVisible
        (progress_view_custom.parent as View).isVisible = isVisible
        progress_action.isEnabled = !isVisible
    }

    // region Mocking

    private val appUpdateInfo
        inline get() = AppUpdateInfo.a(
            "",
            0,
            UpdateAvailability.UPDATE_AVAILABLE,
            InstallStatus.PENDING,
            0,
            0,
            0,
            30000,
            30000,
            30000,
            null,
            null,
            null,
            null
        )

    private fun installState(progress: Long, max: Long) = InstallState.a(
        InstallStatus.DOWNLOADING,
        progress,
        max,
        InstallErrorCode.NO_ERROR,
        ""
    )

    // endregion

}