package com.informatique.electronicmeetingsplatform.di.module

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.content.PartData
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonElement

class AppRepository(val client: HttpClient) {

    suspend fun onGet(url: String): Result<JsonElement> {
        val response = client.get(url)
        return response.runCatching {
            when(status.value){
                200, 201 -> body<JsonElement>()
                else -> throw Exception(status.toString())
            }
        }
    }

    suspend fun onPostAuth(url: String, body: Any): Result<JsonElement> {
        val response = client.post(url) { setBody(body) }
        return response.runCatching {
            when(status.value){
                200, 201 -> body<JsonElement>()
                else -> throw Exception(status.toString())
            }
        }
    }

    // New: JSON-specific POST that sets Content-Type: application/json
    suspend fun onPostAuthJson(url: String, jsonBody: String): Result<JsonElement> {
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(jsonBody)
        }
        return response.runCatching {
            when(status.value){
                200, 201 -> body<JsonElement>()
                else -> throw Exception(status.toString())
            }
        }
    }

    suspend fun onPutAuth(url: String, body: Any): Result<JsonElement> {
        val response = client.put(url) { setBody(body) }
        return response.runCatching {
            when(status.value){
                200, 201 -> body<JsonElement>()
                else -> throw Exception(status.toString())
            }
        }
    }

    suspend fun onPostMultipart(url: String, data: List<PartData>): Result<JsonElement> {
        val response = client.submitFormWithBinaryData(url = url, data)
        return response.runCatching {
            when(status.value){
                200, 201 -> body<JsonElement>()
                else -> throw Exception(status.toString())
            }
        }
    }

    fun onClose(){
        client.close()
    }
}