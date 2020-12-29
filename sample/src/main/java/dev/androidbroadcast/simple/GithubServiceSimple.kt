@file:Suppress("EXPERIMENTAL_API_USAGE")

package dev.androidbroadcast.simple

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException

private const val API_URL = "https://api.github.com"

@Throws(IOException::class)
fun main(): Unit = runBlocking {
    val retrofit = createRetrofit()
    val github = retrofit.create(GitHub::class.java)

    launch {
        try {
            val contributors = github.contributors("kirich1409", "ViewBindingPropertyDelegate")
            contributors.forEach(::println)
        } catch (e: Exception) {
            println("Error $e")
        }
    }
}

private suspend fun launchWithoutHandleException(github: GitHub) {
    val contributors = github.contributors("kirich1409", "ViewBindingPropertyDelegate")
    contributors.forEach(::println)
}

private fun createRetrofit(): Retrofit {
    val json = Json {
        ignoreUnknownKeys = true
    }
    return Retrofit.Builder()
        .baseUrl(API_URL)
        .addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
        .build()
}

@Serializable
data class Contributor(val login: String? = null, val contributions: Int = 0) {

    override fun toString() = "$login (${contributions})"
}

interface GitHub {

    @GET("/repos/{owner}/{repo}/contributors")
    suspend fun contributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): List<Contributor>
}