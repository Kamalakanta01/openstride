package dev.openstride.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.openstride.OpenStrideApplication
import dev.openstride.analysis.Heatmap
import dev.openstride.data.AppRepository
import dev.openstride.data.TrainingAnalysis
import dev.openstride.local.ActivityEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * The state of the training analysis.
 *
 * The analysis is derived from the activities, and it is not cheap: the
 * fitness curve, the weekly load, the records and the badges are all
 * computed from the same activities. It used to be recomputed five times
 * over, on the main thread, every time the screen recomposed. Now it is
 * computed once, off the main thread, when the activities change.
 */
class FitnessViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val repository: AppRepository = (application as OpenStrideApplication).repository

    private val activities = repository.observeActivities()

    /** The analysis, empty until it has been computed. */
    val analysis: StateFlow<TrainingAnalysis> =
        activities
            .map { repository.analysis(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = TrainingAnalysis(),
            )

    /** The activities themselves, for the screens that show them. */
    val activityList: StateFlow<List<ActivityEntity>> =
        activities.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )
}

/**
 * The state of the map screen.
 *
 * The heatmap is rebuilt from every track on the device, so it is kept here
 * rather than recomputed whenever the map recomposes.
 */
class MapViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val repository: AppRepository = (application as OpenStrideApplication).repository

    private val activityIds = MutableStateFlow<List<Long>>(emptyList())

    /** The heatmap cells, empty until they have been computed. */
    val heatmap: StateFlow<List<Heatmap.Cell>> =
        activityIds
            .map { repository.heatmap(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

    /** The activities whose tracks make up the heatmap. */
    fun setActivities(ids: List<Long>) {
        activityIds.value = ids
    }
}
