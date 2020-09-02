package org.iuv.spring

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer

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