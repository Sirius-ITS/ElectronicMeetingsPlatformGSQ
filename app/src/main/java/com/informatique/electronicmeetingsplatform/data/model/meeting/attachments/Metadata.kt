package com.informatique.electronicmeetingsplatform.data.model.meeting.attachments

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Metadata(

    @SerialName("file-extension")
    val fileExtension: String,

    @SerialName("filename-without-extension")
    val filenameWithoutExtension: String,

    @SerialName("mime-main-type")
    val mimeMainType: String,

    @SerialName("mime-sub-type")
    val mimeSubType: String,

    @SerialName("mime-type")
    val mimeType: String,

    @SerialName("processing-date")
    val processingDate: String,

    @SerialName("processing-time")
    val processingTime: String,

    @SerialName("size-category")
    val sizeCategory: String,

    @SerialName("size-human-readable")
    val sizeHumanReadable: String,

    @SerialName("upload-session-id")
    val uploadSessionId: String,

    @SerialName("upload-timestamp")
    val uploadTimestamp: String,

    val uploadedBy: String
)