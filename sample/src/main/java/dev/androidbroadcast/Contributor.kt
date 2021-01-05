package dev.androidbroadcast

import kotlinx.serialization.Serializable

@Serializable
data class Contributor(val login: String? = null, val contributions: Int = 0) {

    override fun toString() = "$login (${contributions})"
}
