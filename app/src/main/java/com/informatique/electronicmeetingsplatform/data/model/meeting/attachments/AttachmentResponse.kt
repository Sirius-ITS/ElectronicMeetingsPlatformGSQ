package com.informatique.electronicmeetingsplatform.data.model.meeting.attachments

import kotlinx.serialization.Serializable

@Serializable
data class AttachmentResponse(
    val bytesTransferred: Int,
    val contentType: String,
    val errorCode: Map<String, String>? = null,
    val errorMessage: Map<String, String>? = null,
    val fileName: String,
    val fileSize: Int,
    val fileUrl: String,
    val message: String,
    val metadata: Metadata,
    val objectKey: String,
    val originalFileName: String,
    val progressPercentage: Int,
    val success: Boolean
) {

    fun getFormattedFileSize(): String {
        val sizeInKB = fileSize / 1024.0
        return if (sizeInKB < 1024) {
            String.format("%.2f KB", sizeInKB)
        } else {
            val sizeInMB = sizeInKB / 1024.0
            String.format("%.2f MB", sizeInMB)
        }
    }

}