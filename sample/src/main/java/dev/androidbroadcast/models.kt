package dev.androidbroadcast

import kotlinx.serialization.Serializable

data class ContributorVO(val login: String?, val contributions: Int)

data class Contributor(val login: String?, val contributions: Int)

@Serializable
data class ContributorDTO(val login: String? = null, val contributions: Int = 0) {

    override fun toString() = "$login (${contributions})"
}