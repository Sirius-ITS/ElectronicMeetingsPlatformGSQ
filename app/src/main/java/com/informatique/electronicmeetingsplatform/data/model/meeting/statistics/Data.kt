package com.informatique.electronicmeetingsplatform.data.model.meeting.statistics

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.roundToInt

object DoubleToIntSerializer : KSerializer<Int?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DoubleToInt", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Int? {
        return try {
            val jsonDecoder = decoder as? JsonDecoder
            val element = jsonDecoder?.decodeJsonElement()
            element?.jsonPrimitive?.content?.toDoubleOrNull()?.roundToInt()
        } catch (e: Exception) {
            null
        }
    }

    override fun serialize(encoder: Encoder, value: Int?) {
        if (value != null) {
            encoder.encodeInt(value)
        }
    }
}

@Serializable
data class Data(
    @Serializable(with = DoubleToIntSerializer::class)
    val meetingsHoursToday: Int?,

    @Serializable(with = DoubleToIntSerializer::class)
    val meetingsTodayCount: Int?,

    val nextOfficialMeeting: NextOfficialMeeting?,

    @Serializable(with = DoubleToIntSerializer::class)
    val upcomingMeetingsCount: Int?,

    @Serializable(with = DoubleToIntSerializer::class)
    val urgentPriorityMeetingsCount: Int?
)