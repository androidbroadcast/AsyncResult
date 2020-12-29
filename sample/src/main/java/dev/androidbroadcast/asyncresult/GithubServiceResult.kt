@file:Suppress("EXPERIMENTAL_API_USAGE")

package dev.androidbroadcast.asyncresult

import by.kirich1409.result.Result
import by.kirich1409.result.asSuccess
import by.kirich1409.result.isSuccess
import by.kirich1409.result.retrofit.ResultAdapterFactory
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
        val result = github.contributors("kirich1409", "ViewBindingPropertyDelegate")
        if (result.isSuccess()) {
            result.asSuccess().value.forEach(::println)
        } else {
            println("Error $result")
        }
    }
}

private fun createRetrofit(): Retrofit {
    val json = Json {
        ignoreUnknownKeys = true
    }
    return Retrofit.Builder()
        .baseUrl(API_URL)
        .addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
        .addCallAdapterFactory(ResultAdapterFactory())
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
    ): Result<List<Contributor>>
}