package dev.openstride.recording

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Restarts recording after a reboot or an app update.
 *
 * Android does not restart foreground services automatically after a reboot, so
 * something has to re-launch them. The receiver only *requests* a resume; the
 * service decides whether there is anything to resume, and stops quietly if not.
 */
class RecoverActivityReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            return
        }
        val serviceIntent =
            Intent(context, RecordingService::class.java).apply {
                putExtra(RecordingService.EXTRA_START_MODE, RecordingService.START_MODE_RESUME)
            }
        context.startForegroundService(serviceIntent)
    }
}
