package com.fatmap.sdk.api

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class VersionInfo(
        val branchName: String,
        val majorVersion: Short,
        val minorVersion: Short,
        val patchVersion: Short,
        val buildVersion: Int,
        val buildDate: String,
        val buildHash: String,
        val buildId: String,
        val versionString: String,
        val environment: Byte
) {
    fun getBranchName(): String = branchName
    fun getBuildDate(): String = buildDate
    fun getBuildHash(): String = buildHash
    fun getBuildId(): String = buildId
    fun getBuildVersion(): Int = buildVersion
    fun getEnvironment(): Byte = environment
    fun getMajorVersion(): Short = majorVersion
    fun getMinorVersion(): Short = minorVersion
    fun getPatchVersion(): Short = patchVersion
    fun getVersionString(): String = versionString

    override fun toString(): String = "VersionInfo(" + listOf(branchName=${branchName}, majorVersion=${majorVersion}, minorVersion=${minorVersion}, patchVersion=${patchVersion}, buildVersion=${buildVersion}, buildDate=${buildDate}, buildHash=${buildHash}, buildId=${buildId}, versionString=${versionString}, environment=${environment}) + ")"
}
