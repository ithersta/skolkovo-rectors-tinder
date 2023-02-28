package common.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.*

object LongRangeSerializer : KSerializer<LongRange> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("LongRange") {
        element("first", serialDescriptor<Long>())
        element("last", serialDescriptor<Long>())
    }

    override fun deserialize(decoder: Decoder) =
        decoder.decodeStructure(descriptor) {
            var first = -1L
            var last = -1L
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> first = decodeLongElement(descriptor, 0)
                    1 -> last = decodeLongElement(descriptor, 1)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index")
                }
            }
            first..last
        }

    override fun serialize(encoder: Encoder, value: LongRange) =
        encoder.encodeStructure(descriptor) {
            encodeLongElement(descriptor, 0, value.first)
            encodeLongElement(descriptor, 1, value.last)
        }
}
