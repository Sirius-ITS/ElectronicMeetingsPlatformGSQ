package com.informatique.electronicmeetingsplatform.data.model.meeting.attachments

import android.net.Uri
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

data class AttachmentRequest(
    val bucket: String = "media",
    val folder: String = "images/avatars",
    val file: Uri,
    val fileName: String,
    val metadata: String = "{\"uploadedBy\":\"mobile_client\"}",
)
