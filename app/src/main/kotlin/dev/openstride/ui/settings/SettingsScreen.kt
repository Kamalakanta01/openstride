package dev.openstride.ui.settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * The settings.
 *
 * The original app's settings are mostly about the account, the privacy of
 * the athlete and the connection to third-party services. There is no
 * account and no third party, so what is left is the units, the map style
 * and the recording behaviour.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val settings = remember { Settings(context) }
    var metric by remember { mutableStateOf(settings.metric) }
    var autoPause by remember { mutableStateOf(settings.autoPause) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Units", style = MaterialTheme.typography.titleSmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = metric,
                    onClick = {
                        metric = true
                        settings.metric = true
                    },
                )
                Text("Metric — kilometres, kilograms")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !metric,
                    onClick = {
                        metric = false
                        settings.metric = false
                    },
                )
                Text("Imperial — miles, pounds")
            }

            Text(
                text = "Recording",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 8.dp),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = autoPause,
                    onClick = {
                        autoPause = true
                        settings.autoPause = true
                    },
                )
                Text("Auto-pause when standing still")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !autoPause,
                    onClick = {
                        autoPause = false
                        settings.autoPause = false
                    },
                )
                Text("Keep recording while standing still")
            }

            Text(
                text =
                    "Everything in this app is stored on this device only. There is no " +
                        "account, no server and no analytics.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

/** The settings the app stores on the device. */
class Settings(
    context: Context,
) {
    private val preferences =
        context.getSharedPreferences("openstride", Context.MODE_PRIVATE)

    /** Whether distances are in kilometres. */
    var metric: Boolean
        get() = preferences.getBoolean("metric", true)
        set(value) = preferences.edit().putBoolean("metric", value).apply()

    /** Whether the recorder pauses itself when the athlete stops moving. */
    var autoPause: Boolean
        get() = preferences.getBoolean("auto_pause", true)
        set(value) = preferences.edit().putBoolean("auto_pause", value).apply()
}
