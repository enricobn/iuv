package org.iuv.shared

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass

open class EnumSerializer<T : Enum<T>>(kClass: KClass<T>, private val values: Array<T>) : KSerializer<T> {
    override fun deserialize(decoder: Decoder): T {
        val s = decoder.decodeString()
        return values.first { it.name == s }
    }

    override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(value.name)

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(kClass.simpleName + "EnumSerializer",
            PrimitiveKind.STRING)

}