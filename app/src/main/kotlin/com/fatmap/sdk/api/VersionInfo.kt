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
    override fun toString(): String = "VersionInfo(branchName=${branchName}, majorVersion=${majorVersion}, minorVersion=${minorVersion}, patchVersion=${patchVersion}, buildVersion=${buildVersion}, buildDate=${buildDate}, buildHash=${buildHash}, buildId=${buildId}, versionString=${versionString}, environment=${environment})"
}
