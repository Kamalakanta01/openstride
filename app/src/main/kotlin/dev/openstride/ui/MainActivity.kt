package dev.openstride.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.openstride.share.ShareActivityScreen
import dev.openstride.ui.activities.ActivityDetailScreen
import dev.openstride.ui.activities.ActivityEditScreen
import dev.openstride.ui.activities.ManualActivityScreen
import dev.openstride.ui.fitness.FitnessScreen
import dev.openstride.ui.log.LogScreen
import dev.openstride.ui.map.MapScreen
import dev.openstride.ui.record.RecordScreen
import dev.openstride.ui.routes.RoutePlannerScreen
import dev.openstride.ui.theme.OpenStrideTheme
import dev.openstride.ui.you.YouScreen
import org.maplibre.android.MapLibre

/**
 * The only activity in the app.
 *
 * Everything else is a screen inside it, so the process holds a single
 * activity and the recording service is the only other component that runs
 * in the foreground.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // MapLibre has to be told where its native library lives before any
        // MapView is created.
        MapLibre.getInstance(this)

        setContent {
            OpenStrideTheme {
                OpenStrideApp()
            }
        }
    }
}

/** The four top-level destinations. */
private enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    MAP("map", "Map", Icons.Filled.Map),
    RECORD("record", "Record", Icons.AutoMirrored.Filled.DirectionsRun),
    LOG("log", "Log", Icons.Filled.Timeline),
    FITNESS("fitness", "Fitness", Icons.Filled.FitnessCenter),
    YOU("you", "You", Icons.Filled.Person),
}

@Composable
fun OpenStrideApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val current = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                Destination.entries.forEach { destination ->
                    val selected = current?.hierarchy?.any { it.route == destination.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        // The label style is deliberately small: this phone
                        // runs at a 1.7x font scale, and the default clips.
                        label = {
                            Text(
                                text = destination.label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.MAP.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Destination.MAP.route) { MapScreen() }
            composable(Destination.RECORD.route) { RecordScreen() }
            composable(Destination.LOG.route) {
                LogScreen(
                    onOpen = { id -> navController.navigate("activity/$id") },
                    onAdd = { navController.navigate("activity/manual") },
                )
            }
            composable("activity/manual") {
                ManualActivityScreen(onDone = { navController.popBackStack() })
            }
            composable("activity/{id}") { entry ->
                val id = entry.arguments?.getString("id")?.toLongOrNull() ?: 0L
                ActivityDetailScreen(
                    activityId = id,
                    onEdit = { navController.navigate("activity/$id/edit") },
                    onShare = { navController.navigate("activity/$id/share") },
                )
            }
            composable("activity/{id}/share") { entry ->
                val id = entry.arguments?.getString("id")?.toLongOrNull() ?: 0L
                ShareActivityScreen(
                    activityId = id,
                    onDone = { navController.popBackStack() },
                )
            }
            composable("activity/{id}/edit") { entry ->
                val id = entry.arguments?.getString("id")?.toLongOrNull() ?: 0L
                ActivityEditScreen(
                    activityId = id,
                    onDone = { navController.popBackStack() },
                )
            }
            composable(Destination.FITNESS.route) { FitnessScreen() }
            composable(Destination.YOU.route) {
                YouScreen(onNewRoute = { navController.navigate("routes/new") })
            }
            composable("routes/new") {
                RoutePlannerScreen(onDone = { navController.popBackStack() })
            }
        }
    }
}
