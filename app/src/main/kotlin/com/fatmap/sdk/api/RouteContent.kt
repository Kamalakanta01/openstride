package com.fatmap.sdk.api

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class RouteContent internal constructor(private val handle: Long) : AutoCloseable {
    private val mProxy = CppProxy(handle)

    /** The native half of the API. One instance per [ RouteContent ]. */
    class CppProxy internal constructor(private val mNativeHandle: Long) {
        external fun native_addWaypointToRoute(handle: Long, text: String, vrouteWaypoint: RouteWaypoint): Unit
        external fun native_displayAnimatedRoute(handle: Long, text: String, vworldLine2: WorldLine2, items: MutableList<RouteSection>, items1: MutableList<RouteWaypoint>, vrouteSelectionStyle: RouteSelectionStyle, flag: Boolean, f: Float, f1: Float, obj: CompletionCallback?): Unit
        external fun native_displayRoute(handle: Long, text: String, vworldLine2: WorldLine2, items: MutableList<RouteSection>, items1: MutableList<RouteWaypoint>, vrouteSelectionStyle: RouteSelectionStyle, flag: Boolean): Unit
        external fun native_displayRouteScrubber(handle: Long, d: Double, d1: Double): Unit
        external fun native_fadeInUnselectedRoutes(handle: Long, f: Float, opt: Float?, obj: CompletionCallback?): Unit
        external fun native_removeAllRoutes(handle: Long): Unit
        external fun native_removeRouteScrubber(handle: Long): Unit
        external fun native_removeRoutes(handle: Long, items: MutableList<String>): Unit
        external fun native_selectRoute(handle: Long, text: String, vrouteSelectionStyle: RouteSelectionStyle): Unit
        external fun native_selectWaypointOnRoute(handle: Long, text: String, text1: String): Unit
        external fun native_stopUnselectedRoutesFadeIn(handle: Long): Unit
        external fun native_unselectRoute(handle: Long, text: String): Unit
        external fun native_unselectWaypointOnRoute(handle: Long, text: String, text1: String): Unit
        external fun nativeDestroy(handle: Long)
    }

    fun addWaypointToRoute(text: String, vrouteWaypoint: RouteWaypoint): Unit = mProxy.native_addWaypointToRoute(handle, text, vrouteWaypoint)
    fun displayAnimatedRoute(text: String, vworldLine2: WorldLine2, items: MutableList<RouteSection>, items1: MutableList<RouteWaypoint>, vrouteSelectionStyle: RouteSelectionStyle, flag: Boolean, f: Float, f1: Float, obj: CompletionCallback?): Unit = mProxy.native_displayAnimatedRoute(handle, text, vworldLine2, items, items1, vrouteSelectionStyle, flag, f, f1, obj)
    fun displayRoute(text: String, vworldLine2: WorldLine2, items: MutableList<RouteSection>, items1: MutableList<RouteWaypoint>, vrouteSelectionStyle: RouteSelectionStyle, flag: Boolean): Unit = mProxy.native_displayRoute(handle, text, vworldLine2, items, items1, vrouteSelectionStyle, flag)
    fun displayRouteScrubber(d: Double, d1: Double): Unit = mProxy.native_displayRouteScrubber(handle, d, d1)
    fun fadeInUnselectedRoutes(f: Float, opt: Float?, obj: CompletionCallback?): Unit = mProxy.native_fadeInUnselectedRoutes(handle, f, opt, obj)
    fun removeAllRoutes(): Unit = mProxy.native_removeAllRoutes(handle)
    fun removeRouteScrubber(): Unit = mProxy.native_removeRouteScrubber(handle)
    fun removeRoutes(items: MutableList<String>): Unit = mProxy.native_removeRoutes(handle, items)
    fun selectRoute(text: String, vrouteSelectionStyle: RouteSelectionStyle): Unit = mProxy.native_selectRoute(handle, text, vrouteSelectionStyle)
    fun selectWaypointOnRoute(text: String, text1: String): Unit = mProxy.native_selectWaypointOnRoute(handle, text, text1)
    fun stopUnselectedRoutesFadeIn(): Unit = mProxy.native_stopUnselectedRoutesFadeIn(handle)
    fun unselectRoute(text: String): Unit = mProxy.native_unselectRoute(handle, text)
    fun unselectWaypointOnRoute(text: String, text1: String): Unit = mProxy.native_unselectWaypointOnRoute(handle, text, text1)

    override fun close() {
        mProxy.nativeDestroy(handle)
    }
}
