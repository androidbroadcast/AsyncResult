@file:Suppress("EXPERIMENTAL_API_USAGE")

package dev.androidbroadcast

import by.kirich1409.result.Result
import by.kirich1409.result.asSuccess
import by.kirich1409.result.isSuccess
import by.kirich1409.result.retrofit.ResultAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException

interface GitHub {

    @GET("/repos/{owner}/{repo}/contributors")
    suspend fun contributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Result<List<ContributorDTO>>
}


@Throws(IOException::class)
fun main(): Unit = runBlocking {
    val retrofit = createRetrofit()
    val github = retrofit.create(GitHub::class.java)

    val callResult: Result<List<ContributorDTO>> = github.contributors("kirich1409", "ViewBindingPropertyDelegate")
    if (callResult.isSuccess()) {
        val contributors = callResult.asSuccess().value
        print(contributors)
    } else {
        print("Call error: $callResult")
    }
}


private fun createRetrofit(): Retrofit {
    val json = Json {
        ignoreUnknownKeys = true
    }

    return Retrofit.Builder()
        .baseUrl("https://api.github.com")
        .addCallAdapterFactory(ResultAdapterFactory())
        .addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
        .build()
}