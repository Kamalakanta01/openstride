package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class MemoryStats(
    val currentAllocationCount: Long,
    val currentAllocatedMb: Double,
    val totalAllocationCount: Long,
    val totalAllocatedMb: Double
) {
    override fun toString(): String = "MemoryStats(currentAllocationCount=${currentAllocationCount}, currentAllocatedMb=${currentAllocatedMb}, totalAllocationCount=${totalAllocationCount}, totalAllocatedMb=${totalAllocatedMb})"
}
