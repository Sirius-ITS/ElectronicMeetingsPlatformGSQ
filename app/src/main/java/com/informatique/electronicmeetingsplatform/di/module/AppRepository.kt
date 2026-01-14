package com.informatique.electronicmeetingsplatform.di.module

import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.content.PartData
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import java.io.IOException

class AppRepository(val client: HttpClient) {

    suspend fun onGet(url: String): ApiResponse<JsonElement> {
        return safeApiCall {
            val response = client.get(url)
            when (response.status.value) {
                in 200..299 -> {
                    val response = response.body<JsonElement>()
                    ApiResponse.Success(response)
                }

                401 -> {
                    val errorBody = response.body<ErrorResponse>()
                    ApiResponse.Error(
                        message = errorBody.message ?: "Invalid credentials",
                        code = 401
                    )
                }

                else -> {
                    val errorBody = runCatching { response.body<ErrorResponse>() }.getOrNull()
                    ApiResponse.Error(
                        message = errorBody?.message ?: "Get data failed",
                        code = response.status.value
                    )
                }
            }
        }
    }

    suspend fun onPost(url: String, body: Any): ApiResponse<JsonElement> {
        return safeApiCall {
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            when (response.status.value) {
                in 200..299 -> {
                    val response = response.body<JsonElement>()
                    ApiResponse.Success(response)
                }

                401 -> {
                    val errorBody = response.body<ErrorResponse>()
                    ApiResponse.Error(
                        message = errorBody.message ?: "Invalid credentials",
                        code = 401
                    )
                }

                else -> {
                    val errorBody = runCatching { response.body<ErrorResponse>() }.getOrNull()
                    ApiResponse.Error(
                        message = errorBody?.message ?: "Post auth data failed",
                        code = response.status.value
                    )
                }
            }
        }
    }

    // New: JSON-specific POST that sets Content-Type: application/json
    suspend fun onPostAuthJson(url: String, jsonBody: String): ApiResponse<JsonElement> {
        return safeApiCall {
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(jsonBody)
            }
            when (response.status.value) {
                in 200..299 -> {
                    val response = response.body<JsonElement>()
                    ApiResponse.Success(response)
                }

                401 -> {
                    val errorBody = response.body<ErrorResponse>()
                    ApiResponse.Error(
                        message = errorBody.message ?: "Invalid credentials",
                        code = 401
                    )
                }

                else -> {
                    val errorBody = runCatching { response.body<ErrorResponse>() }.getOrNull()
                    ApiResponse.Error(
                        message = errorBody?.message ?: "Post auth json failed",
                        code = response.status.value
                    )
                }
            }
        }
    }

    suspend fun onPutAuth(url: String, body: Any): ApiResponse<JsonElement> {
        return safeApiCall {
            val response = client.put(url) { setBody(body) }
            when (response.status.value) {
                in 200..299 -> {
                    val response = response.body<JsonElement>()
                    ApiResponse.Success(response)
                }

                401 -> {
                    val errorBody = response.body<ErrorResponse>()
                    ApiResponse.Error(
                        message = errorBody.message ?: "Invalid credentials",
                        code = 401
                    )
                }

                else -> {
                    val errorBody = runCatching { response.body<ErrorResponse>() }.getOrNull()
                    ApiResponse.Error(
                        message = errorBody?.message ?: "Put auth failed",
                        code = response.status.value
                    )
                }
            }
        }
    }

    suspend fun onPostMultipart(url: String, data: List<PartData>): ApiResponse<JsonElement> {
        return safeApiCall {
            val response = client.submitFormWithBinaryData(url = url, data)
            when (response.status.value) {
                in 200..299 -> {
                    val response = response.body<JsonElement>()
                    ApiResponse.Success(response)
                }
                401 -> {
                    val errorBody = response.body<ErrorResponse>()
                    ApiResponse.Error(
                        message = errorBody.message ?: "Invalid credentials",
                        code = 401
                    )
                }
                else -> {
                    val errorBody = runCatching { response.body<ErrorResponse>() }.getOrNull()
                    ApiResponse.Error(
                        message = errorBody?.message ?: "Post multipart failed",
                        code = response.status.value
                    )
                }
            }
        }
    }

    fun onClose(){
        client.close()
    }

    /**
     * Safe API call wrapper with comprehensive error handling
     * Catches and transforms all possible exceptions into ApiResponse types
     */
    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> ApiResponse<T>
    ): ApiResponse<T> {
        return try {
            apiCall()
        } catch (e: ClientRequestException) {
            // 4xx errors
            ApiResponse.Error(
                message = e.response.body<ErrorResponse>().message ?: "Client error: ${e.message}",
                code = e.response.status.value,
                exception = e
            )
        } catch (e: ServerResponseException) {
            // 5xx errors
            ApiResponse.Error(
                message = "Server error: ${e.message}",
                code = e.response.status.value,
                exception = e
            )
        } catch (e: IOException) {
            // Network errors
            ApiResponse.NetworkError(e)
        } catch (e: CancellationException) {
            // Coroutine cancellation - rethrow
            throw e
        } catch (e: Exception) {
            // Generic errors
            ApiResponse.Error(
                message = e.message ?: "Unknown error occurred",
                exception = e
            )
        }
    }

    /**
     * Standard error response structure
     */
    @Serializable
    private data class ErrorResponse(
        @SerialName("message")
        val message: String? = null,

        @SerialName("error")
        val error: String? = null,

        @SerialName("errors")
        val errors: Map<String, List<String>>? = null
    )
}
