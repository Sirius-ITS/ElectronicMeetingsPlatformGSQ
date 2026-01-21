package com.informatique.electronicmeetingsplatform.data.model.meeting.attachments

import kotlinx.serialization.Serializable

@Serializable
data class DeleteAttachmentRequest(
    val fileName: String
)
