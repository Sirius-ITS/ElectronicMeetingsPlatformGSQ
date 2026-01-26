package com.informatique.electronicmeetingsplatform.data.remote.meeting

import android.content.Context
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.AllMeetingDetailResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.AllMeetingResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.RespondMeetingRequest
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.RespondMeetingResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.attachments.AttachmentRequest
import com.informatique.electronicmeetingsplatform.data.model.meeting.attachments.AttachmentResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.attachments.DeleteAttachmentRequest
import com.informatique.electronicmeetingsplatform.data.model.meeting.attachments.DeleteAttachmentResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.create.CreateMeetingRequest
import com.informatique.electronicmeetingsplatform.data.model.meeting.create.CreateMeetingResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.invitees.InviteesResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.priorities.MeetingPrioritiesResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.statistics.MeetingStatisticsResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.type.MeetingTypeResponse
import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse
import com.informatique.electronicmeetingsplatform.di.module.AppRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.append
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MeetingApiService using Ktor HttpClient
 * Handles all meetings requests with advanced error handling
 */
@Singleton
class MeetingApiServiceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val appRepository: AppRepository,
    private val json: Json
) : MeetingApiService {

    companion object {
        private const val MEETING_STATISTICS_ENDPOINT = "api/meetings/v1/mobile/statistics"

        private const val MEETING_TYPES_ENDPOINT = "api/meetings/v1/mobile/meeting-types"

        private const val MEETING_PRIORITIES_ENDPOINT = "api/meetings/v1/mobile/meeting-priorities"

        private const val MEETING_INVITEES_ENDPOINT = "api/identity/v1/mobile/persons/search?page=1&returnAll=true"

        private const val MEETING_ATTACHMENTS_ENDPOINT = "api/media/mobile/upload"

        private const val DELETE_ATTACHMENT_ENDPOINT = "api/media/mobile/delete"

        private const val CREATE_MEETING_ENDPOINT = "api/meetings/v1/mobile/meetings"

        private const val ALL_MEETINGS_ENDPOINT = "api/meetings/v1/mobile/my-next-meetings"

        private const val ALL_MEETING_DETAIL_ENDPOINT = "api/meetings/v1/mobile/special/"

        private const val RESPOND_MEETING_ENDPOINT = "api/meetings/v1/mobile/meetings/"
    }

    override suspend fun meetingStatistics(): ApiResponse<MeetingStatisticsResponse> {
        return when (val response = appRepository.onGet(MEETING_STATISTICS_ENDPOINT)) {
            is ApiResponse.Success -> {
                try {
                    ApiResponse.Success(
                        json.decodeFromJsonElement(
                            MeetingStatisticsResponse.serializer(),
                            response.data
                        )
                    )
                } catch (e: SerializationException) {
                    e.printStackTrace()
                    ApiResponse.Error(
                        message = "Invalid response format: ${e.message}",
                        code = -1
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    ApiResponse.Error(
                        message = "UnHandled error: ${e.message}",
                        code = -1
                    )
                }
            }

            else -> {
                val errorBody = when (response) {
                    is ApiResponse.Error -> null
                    is ApiResponse.NetworkError -> null
                    else -> null
                }
                ApiResponse.Error(
                    message = errorBody ?: "Meeting statistics failed",
                    code = when (response) {
                        is ApiResponse.Error -> response.code
                        else -> -1
                    }
                )
            }
        }
    }

    override suspend fun meetingTypes(): ApiResponse<MeetingTypeResponse> {
        return when (val response = appRepository.onGet(MEETING_TYPES_ENDPOINT)) {
            is ApiResponse.Success -> {
                try {
                    ApiResponse.Success(
                        json.decodeFromJsonElement(
                            MeetingTypeResponse.serializer(),
                            response.data
                        )
                    )
                } catch (e: SerializationException) {
                    ApiResponse.Error(
                        message = "Invalid response format: ${e.message}",
                        code = -1
                    )
                } catch (e: Exception) {
                    ApiResponse.Error(
                        message = "UnHandled error: ${e.message}",
                        code = -1
                    )
                }
            }

            else -> {
                val errorBody = when (response) {
                    is ApiResponse.Error -> null
                    is ApiResponse.NetworkError -> null
                    else -> null
                }
                ApiResponse.Error(
                    message = errorBody ?: "Meeting types failed",
                    code = when (response) {
                        is ApiResponse.Error -> response.code
                        else -> -1
                    }
                )
            }
        }
    }

    override suspend fun meetingPriorities(): ApiResponse<MeetingPrioritiesResponse> {
        return when (val response = appRepository.onGet(MEETING_PRIORITIES_ENDPOINT)) {
            is ApiResponse.Success -> {
                return try {
                    ApiResponse.Success(
                        json.decodeFromJsonElement(
                            MeetingPrioritiesResponse.serializer(),
                            response.data
                        )
                    )
                } catch (e: SerializationException) {
                    ApiResponse.Error(
                        message = "Invalid response format: ${e.message}",
                        code = -1
                    )
                } catch (e: Exception) {
                    ApiResponse.Error(
                        message = "UnHandled error: ${e.message}",
                        code = -1
                    )
                }
            }

            else -> {
                val errorBody = when (response) {
                    is ApiResponse.Error -> null
                    is ApiResponse.NetworkError -> null
                    else -> null
                }
                ApiResponse.Error(
                    message = errorBody ?: "Meeting priorities failed",
                    code = when (response) {
                        is ApiResponse.Error -> response.code
                        else -> -1
                    }
                )
            }
        }
    }

    override suspend fun meetingInvitees(): ApiResponse<InviteesResponse> {
        return when (val response = appRepository.onGet(url = MEETING_INVITEES_ENDPOINT)) {
            is ApiResponse.Success -> {
                return try {
                    ApiResponse.Success(
                        json.decodeFromJsonElement(
                            InviteesResponse.serializer(),
                            response.data
                        )
                    )
                } catch (e: SerializationException) {
                    ApiResponse.Error(
                        message = "Invalid response format: ${e.message}",
                        code = -1
                    )
                } catch (e: Exception) {
                    ApiResponse.Error(
                        message = "UnHandled error: ${e.message}",
                        code = -1
                    )
                }
            }

            else -> {
                val errorBody = when (response) {
                    is ApiResponse.Error -> null
                    is ApiResponse.NetworkError -> null
                    else -> null
                }
                ApiResponse.Error(
                    message = errorBody ?: "Meeting invitees failed",
                    code = when (response) {
                        is ApiResponse.Error -> response.code
                        else -> -1
                    }
                )
            }
        }
    }

    override suspend fun meetingAttachments(request: AttachmentRequest): ApiResponse<AttachmentResponse> {
        val multiPartData = formData {
            // Read bytes from URI using ContentResolver instead of direct file access
            val bytes = context.contentResolver.openInputStream(request.file)?.use { inputStream ->
                inputStream.readBytes()
            } ?: byteArrayOf()

            append(
                "File",
                bytes,
                Headers.build {
                    append(HttpHeaders.ContentType, ContentType.Image.JPEG)
                    append(HttpHeaders.ContentDisposition, "filename=\"${request.fileName}\"")
                }
            )
            append("Metadata", request.metadata)
            append("Bucket", request.bucket)
            append("Folder", request.folder)
        }

        return when (val response = appRepository.onPostMultipart(
            url = MEETING_ATTACHMENTS_ENDPOINT, data = multiPartData)) {
            is ApiResponse.Success -> {
                try {
                    ApiResponse.Success(
                        json.decodeFromJsonElement(
                            AttachmentResponse.serializer(), response.data
                        )
                    )
                } catch (e: SerializationException) {
                    ApiResponse.Error(
                        message = "Invalid response format: ${e.message}",
                        code = -1
                    )
                } catch (e: Exception) {
                    ApiResponse.Error(
                        message = "UnHandled error: ${e.message}",
                        code = -1
                    )
                }
            }

            else -> {
                val errorBody = when (response) {
                    is ApiResponse.Error -> null
                    is ApiResponse.NetworkError -> null
                    else -> null
                }
                ApiResponse.Error(
                    message = errorBody ?: "Upload attachment failed",
                    code = when (response) {
                        is ApiResponse.Error -> response.code
                        else -> -1
                    }
                )
            }
        }
    }

    override suspend fun deleteAttachment(request: DeleteAttachmentRequest): ApiResponse<DeleteAttachmentResponse> {
        return when (val response = appRepository.onPost(DELETE_ATTACHMENT_ENDPOINT, request)) {
            is ApiResponse.Success -> {
                return try {
                    ApiResponse.Success(
                        json.decodeFromJsonElement(
                            DeleteAttachmentResponse.serializer(), response.data
                        )
                    )
                } catch (e: SerializationException) {
                    ApiResponse.Error(
                        message = "Invalid response format: ${e.message}",
                        code = -1
                    )
                } catch (e: Exception) {
                    ApiResponse.Error(
                        message = "UnHandled error: ${e.message}",
                        code = -1
                    )
                }
            }

            else -> {
                val errorBody = when (response) {
                    is ApiResponse.Error -> null
                    is ApiResponse.NetworkError -> null
                    else -> null
                }
                ApiResponse.Error(
                    message = errorBody ?: "Delete attachment failed",
                    code = when (response) {
                        is ApiResponse.Error -> response.code
                        else -> -1
                    }
                )
            }
        }
    }

    override suspend fun createMeeting(request: CreateMeetingRequest): ApiResponse<CreateMeetingResponse> {
        return when (val response = appRepository.onPost(CREATE_MEETING_ENDPOINT, request)) {
            is ApiResponse.Success -> {
                return try {
                    ApiResponse.Success(
                        json.decodeFromJsonElement(
                            CreateMeetingResponse.serializer(), response.data
                        )
                    )
                } catch (e: SerializationException) {
                    ApiResponse.Error(
                        message = "Invalid response format: ${e.message}",
                        code = -1
                    )
                } catch (e: Exception) {
                    ApiResponse.Error(
                        message = "UnHandled error: ${e.message}",
                        code = -1
                    )
                }
            }

            else -> {
                val errorBody = when (response) {
                    is ApiResponse.Error -> null
                    is ApiResponse.NetworkError -> null
                    else -> null
                }
                ApiResponse.Error(
                    message = errorBody ?: "Create meeting failed",
                    code = when (response) {
                        is ApiResponse.Error -> response.code
                        else -> -1
                    }
                )
            }
        }
    }

    override suspend fun allMeetings(): ApiResponse<AllMeetingResponse> {
        return when (val response = appRepository.onGet(ALL_MEETINGS_ENDPOINT)) {
            is ApiResponse.Success -> {
                try {
                    ApiResponse.Success(
                        json.decodeFromJsonElement(
                            AllMeetingResponse.serializer(),
                            response.data
                        )
                    )
                } catch (e: SerializationException) {
                    e.printStackTrace()
                    ApiResponse.Error(
                        message = "Invalid response format: ${e.message}",
                        code = -1
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    ApiResponse.Error(
                        message = "UnHandled error: ${e.message}",
                        code = -1
                    )
                }
            }

            else -> {
                val errorBody = when (response) {
                    is ApiResponse.Error -> null
                    is ApiResponse.NetworkError -> null
                    else -> null
                }
                ApiResponse.Error(
                    message = errorBody ?: "All Meetings failed",
                    code = when (response) {
                        is ApiResponse.Error -> response.code
                        else -> -1
                    }
                )
            }
        }
    }

    override suspend fun allMeetingDetail(meetingId: Int): ApiResponse<AllMeetingDetailResponse> {
        return when (val response = appRepository.onGet(ALL_MEETING_DETAIL_ENDPOINT.plus(meetingId))) {
            is ApiResponse.Success -> {
                try {
                    ApiResponse.Success(
                        json.decodeFromJsonElement(
                            AllMeetingDetailResponse.serializer(),
                            response.data
                        )
                    )
                } catch (e: SerializationException) {
                    e.printStackTrace()
                    ApiResponse.Error(
                        message = "Invalid response format: ${e.message}",
                        code = -1
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    ApiResponse.Error(
                        message = "UnHandled error: ${e.message}",
                        code = -1
                    )
                }
            }

            else -> {
                val errorBody = when (response) {
                    is ApiResponse.Error -> null
                    is ApiResponse.NetworkError -> null
                    else -> null
                }
                ApiResponse.Error(
                    message = errorBody ?: "All meeting detail failed",
                    code = when (response) {
                        is ApiResponse.Error -> response.code
                        else -> -1
                    }
                )
            }
        }
    }

    override suspend fun meetingRespondStatus(respond: RespondMeetingRequest): ApiResponse<RespondMeetingResponse> {
        return when (val response = appRepository.onPost(
            url = RESPOND_MEETING_ENDPOINT.plus("${respond.meetingId}/respond"),
            body = respond)) {
            is ApiResponse.Success -> {
                return try {
                    ApiResponse.Success(
                        json.decodeFromJsonElement(
                            RespondMeetingResponse.serializer(), response.data
                        )
                    )
                } catch (e: SerializationException) {
                    ApiResponse.Error(
                        message = "Invalid response format: ${e.message}",
                        code = -1
                    )
                } catch (e: Exception) {
                    ApiResponse.Error(
                        message = "UnHandled error: ${e.message}",
                        code = -1
                    )
                }
            }

            else -> {
                val errorBody = when (response) {
                    is ApiResponse.Error -> null
                    is ApiResponse.NetworkError -> null
                    else -> null
                }
                ApiResponse.Error(
                    message = errorBody ?: "Respond meeting failed",
                    code = when (response) {
                        is ApiResponse.Error -> response.code
                        else -> -1
                    }
                )
            }
        }
    }

}

