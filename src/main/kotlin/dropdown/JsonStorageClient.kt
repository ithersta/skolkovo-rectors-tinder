package dropdown

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

class JsonStorageClient(
    val host: String = "https://petrobot-jsonstorage.shuttleapp.rs/",
    val client: HttpClient = HttpClient(OkHttp) {
        expectSuccess = true
        install(ContentNegotiation) {
            json()
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
