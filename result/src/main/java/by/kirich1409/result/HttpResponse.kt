package by.kirich1409.result

interface HttpResponse {

    val statusCode: Int

    val statusMessage: String?

    val url: String?
}
