package by.kirich1409.asyncresult

import by.kirich1409.result.Result
import by.kirich1409.result.asSuccess
import by.kirich1409.result.isSuccess
import by.kirich1409.result.retrofit.ResultAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException

private const val API_URL = "https://api.github.com"

@Throws(IOException::class)
fun main() {
    val retrofit = createRetrofit()
    val github = retrofit.create(GitHub::class.java)

    val result = github.contributors("kirich1409", "ViewBindingPropertyDelegate")
    if (result.isSuccess()) {
        result.asSuccess().value.forEach(::println)
    } else {
        println("Error $result")
    }
}

private fun createRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(ResultAdapterFactory())
        .build()
}

data class Contributor(val login: String? = null, val contributions: Int = 0) {

    override fun toString() = "$login (${contributions})"
}

interface GitHub {

    @GET("/repos/{owner}/{repo}/contributors")
    fun contributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Result<List<Contributor>>
}