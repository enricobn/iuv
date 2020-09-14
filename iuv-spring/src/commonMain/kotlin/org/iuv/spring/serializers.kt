package org.iuv.spring

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

object IntIUVSerializer : IUVSerializer<Int> {
    override val serializer: KSerializer<Int>
        get() = Int.serializer()
}

object UnitIUVSerializer : IUVSerializer<Unit> {
    override val serializer: KSerializer<Unit>
        get() = Unit.serializer()
}

object StringIUVSerializer : IUVSerializer<String> {
    override val serializer: KSerializer<String>
        get() = String.serializer()
}

@InternalSerializationApi
open class KClassSerializer<T : Any>(private val kClass: KClass<T>) : IUVSerializer<T> {

    override val serializer: KSerializer<T>
        get() = kClass.serializer()

}