package org.iuv.spring

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

object IntIUVSerializer : IUVSerializer {
    override val serializer: KSerializer<*>
        get() = Int.serializer()
}

object UnitIUVSerializer : IUVSerializer {
    override val serializer: KSerializer<*>
        get() = Unit.serializer()
}

object StringIUVSerializer : IUVSerializer {
    override val serializer: KSerializer<*>
        get() = String.serializer()
}

@InternalSerializationApi
open class KClassSerializer(private val kClass: KClass<*>) : IUVSerializer {

    override val serializer: KSerializer<*>
        get() = kClass.serializer()

}