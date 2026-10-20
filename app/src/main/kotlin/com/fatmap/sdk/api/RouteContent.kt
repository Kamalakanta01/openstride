package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class RouteContent internal constructor(handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ RouteContent ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_addWaypointToRoute(text: String, vrouteWaypoint: RouteWaypoint): Unit
        external fun native_displayAnimatedRoute(text: String, vworldLine2: WorldLine2, items: MutableList<RouteSection>, items1: MutableList<RouteWaypoint>, vrouteSelectionStyle: RouteSelectionStyle, flag: Boolean, f: Float, f1: Float, obj: CompletionCallback): Unit
        external fun native_displayRoute(text: String, vworldLine2: WorldLine2, items: MutableList<RouteSection>, items1: MutableList<RouteWaypoint>, vrouteSelectionStyle: RouteSelectionStyle, flag: Boolean): Unit
        external fun native_displayRouteScrubber(d: Double, d1: Double): Unit
        external fun native_fadeInUnselectedRoutes(f: Float, opt: Float?, obj: CompletionCallback): Unit
        external fun native_removeAllRoutes(): Unit
        external fun native_removeRouteScrubber(): Unit
        external fun native_removeRoutes(items: MutableList<String>): Unit
        external fun native_selectRoute(text: String, vrouteSelectionStyle: RouteSelectionStyle): Unit
        external fun native_selectWaypointOnRoute(text: String, text1: String): Unit
        external fun native_stopUnselectedRoutesFadeIn(): Unit
        external fun native_unselectRoute(text: String): Unit
        external fun native_unselectWaypointOnRoute(text: String, text1: String): Unit
        external fun nativeDestroy()
    }

    fun addWaypointToRoute(text: String, vrouteWaypoint: RouteWaypoint): Unit = mProxy.native_addWaypointToRoute(text, vrouteWaypoint)
    fun displayAnimatedRoute(text: String, vworldLine2: WorldLine2, items: MutableList<RouteSection>, items1: MutableList<RouteWaypoint>, vrouteSelectionStyle: RouteSelectionStyle, flag: Boolean, f: Float, f1: Float, obj: CompletionCallback): Unit = mProxy.native_displayAnimatedRoute(text, vworldLine2, items, items1, vrouteSelectionStyle, flag, f, f1, obj)
    fun displayRoute(text: String, vworldLine2: WorldLine2, items: MutableList<RouteSection>, items1: MutableList<RouteWaypoint>, vrouteSelectionStyle: RouteSelectionStyle, flag: Boolean): Unit = mProxy.native_displayRoute(text, vworldLine2, items, items1, vrouteSelectionStyle, flag)
    fun displayRouteScrubber(d: Double, d1: Double): Unit = mProxy.native_displayRouteScrubber(d, d1)
    fun fadeInUnselectedRoutes(f: Float, opt: Float?, obj: CompletionCallback): Unit = mProxy.native_fadeInUnselectedRoutes(f, opt, obj)
    fun removeAllRoutes(): Unit = mProxy.native_removeAllRoutes()
    fun removeRouteScrubber(): Unit = mProxy.native_removeRouteScrubber()
    fun removeRoutes(items: MutableList<String>): Unit = mProxy.native_removeRoutes(items)
    fun selectRoute(text: String, vrouteSelectionStyle: RouteSelectionStyle): Unit = mProxy.native_selectRoute(text, vrouteSelectionStyle)
    fun selectWaypointOnRoute(text: String, text1: String): Unit = mProxy.native_selectWaypointOnRoute(text, text1)
    fun stopUnselectedRoutesFadeIn(): Unit = mProxy.native_stopUnselectedRoutesFadeIn()
    fun unselectRoute(text: String): Unit = mProxy.native_unselectRoute(text)
    fun unselectWaypointOnRoute(text: String, text1: String): Unit = mProxy.native_unselectWaypointOnRoute(text, text1)

    override fun close() {
        mProxy.nativeDestroy()
    }
}
