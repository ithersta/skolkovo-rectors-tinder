package dropdown

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

private const val TIMEOUT_MILLIS = 1000L

class JsonStorageClient(
    val host: String = "https://petrobot-jsonstorage.shuttleapp.rs/",
    val client: HttpClient = HttpClient(OkHttp) {
        expectSuccess = true
        install(ContentNegotiation) {
            json()
        }
        install(HttpRequestRetry) {
            retryIf(maxRetries = 30) { _, response ->
                response.status.isSuccess().not()
            }
            retryOnException(retryOnTimeout = true)
            constantDelay()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT_MILLIS
            socketTimeoutMillis = TIMEOUT_MILLIS
            connectTimeoutMillis = TIMEOUT_MILLIS
        }
    }
) {
    suspend inline fun <reified T> storeAndGetUrl(data: T): String {
        val response = client.post(host) {
            contentType(ContentType.Application.Json)
            setBody(data)
        }
        return host + response.bodyAsText()
    }
}
