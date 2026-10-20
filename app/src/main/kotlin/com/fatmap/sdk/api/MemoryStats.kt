package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class MemoryStats(
        val currentAllocationCount: Long,
        val currentAllocatedMb: Double,
        val totalAllocationCount: Long,
        val totalAllocatedMb: Double
) {
    fun getCurrentAllocatedMb(): Double = currentAllocatedMb
    fun getCurrentAllocationCount(): Long = currentAllocationCount
    fun getTotalAllocatedMb(): Double = totalAllocatedMb
    fun getTotalAllocationCount(): Long = totalAllocationCount

    override fun toString(): String = "MemoryStats(" + listOf(currentAllocationCount=${currentAllocationCount}, currentAllocatedMb=${currentAllocatedMb}, totalAllocationCount=${totalAllocationCount}, totalAllocatedMb=${totalAllocatedMb}) + ")"
}
