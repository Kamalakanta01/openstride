package dev.openstride.ui.you

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.openstride.OpenStrideApplication
import dev.openstride.ui.gear.GearScreen
import dev.openstride.ui.goals.GoalScreen
import dev.openstride.ui.privacy.PrivacyScreen
import dev.openstride.ui.profile.ProfileScreen
import dev.openstride.ui.routes.RouteScreen
import dev.openstride.ui.segments.SegmentScreen
import dev.openstride.ui.settings.SettingsScreen

/**
 * The athlete's own corner of the app.
 *
 * The original app's "You" tab holds the profile, the gear, the goals, the
 * friends and the settings. Everything that needs other people is gone;
 * everything that is about the athlete is here.
 */
@Composable
fun YouScreen() {
    val context = LocalContext.current
    val repository = (context.applicationContext as OpenStrideApplication).repository

    val activities by repository.observeActivities().collectAsState(initial = emptyList())
    val gear by repository.observeGear().collectAsState(initial = emptyList())
    val goals by repository.observeGoals().collectAsState(initial = emptyList())
    val segments by repository.observeSegments().collectAsState(initial = emptyList())
    val routes by repository.observeRoutes().collectAsState(initial = emptyList())

    var page by remember { mutableStateOf(Page.PROFILE) }

    when (page) {
        Page.PROFILE ->
            YouMenu(
                activityCount = activities.size,
                gearCount = gear.size,
                goalCount = goals.size,
                segmentCount = segments.size,
                routeCount = routes.size,
                onOpen = { page = it },
            )
        Page.GEAR -> GearScreen(onBack = { page = Page.PROFILE })
        Page.GOALS -> GoalScreen(onBack = { page = Page.PROFILE })
        Page.SEGMENTS -> SegmentScreen(onBack = { page = Page.PROFILE })
        Page.ROUTES -> RouteScreen(onBack = { page = Page.PROFILE })
        Page.PRIVACY -> PrivacyScreen(onBack = { page = Page.PROFILE })
        Page.SETTINGS -> SettingsScreen(onBack = { page = Page.PROFILE })
    }
}

/** The pages under the "You" tab. */
enum class Page {
    PROFILE,
    GEAR,
    GOALS,
    SEGMENTS,
    ROUTES,
    PRIVACY,
    SETTINGS,
}

/** The list of things the athlete can open. */
@Composable
private fun YouMenu(
    activityCount: Int,
    gearCount: Int,
    goalCount: Int,
    segmentCount: Int,
    routeCount: Int,
    onOpen: (Page) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding =
            androidx.compose.foundation.layout
                .PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            ProfileScreen()
        }
        item {
            Text(
                text = "Your things",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
        item {
            MenuRow(Icons.Filled.Settings, "Settings", "Units, privacy defaults", null) { onOpen(Page.SETTINGS) }
        }
        item {
            MenuRow(Icons.Filled.EmojiEvents, "Gear", "Bikes, shoes and the distance they have covered", "$gearCount") {
                onOpen(Page.GEAR)
            }
        }
        item {
            MenuRow(Icons.Filled.Flag, "Goals", "What you are aiming at this week and month", "$goalCount") {
                onOpen(Page.GOALS)
            }
        }
        item {
            MenuRow(Icons.Filled.Celebration, "Local segments", "Streets you keep coming back to", "$segmentCount") {
                onOpen(Page.SEGMENTS)
            }
        }
        item {
            MenuRow(Icons.Filled.Map, "Routes", "Routes you have saved", "$routeCount") { onOpen(Page.ROUTES) }
        }
        item {
            MenuRow(Icons.Filled.VisibilityOff, "Privacy zones", "Places that are hidden from your public activities", null) {
                onOpen(Page.PRIVACY)
            }
        }
        item {
            Text(
                text =
                    "This app has no server. Your activities, gear, goals and routes stay on this " +
                        "device, and there is nothing to sign in to.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

/** One row of the menu. */
@Composable
private fun MenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    count: String?,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(start = 14.dp),
            ) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (count != null) {
                Text(
                    text = count,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
