package com.informatique.electronicmeetingsplatform.data.model.meeting.attachments

import kotlinx.serialization.Serializable

@Serializable
data class DeleteAttachmentResponse(
    val success: Boolean,
    val message: String
)
