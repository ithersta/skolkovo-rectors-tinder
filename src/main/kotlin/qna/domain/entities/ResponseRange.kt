package qna.domain.entities

import common.serializers.LongRangeSerializer
import kotlinx.serialization.Serializable

@Serializable
class ResponseRange(
    @Serializable(with = LongRangeSerializer::class)
    val idRange: LongRange,
    val questionId: Long
)
