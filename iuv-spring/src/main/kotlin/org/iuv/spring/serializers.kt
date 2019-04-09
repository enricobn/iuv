package org.iuv.spring

import kotlinx.serialization.KSerializer
import kotlinx.serialization.internal.IntSerializer
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.internal.UnitSerializer

object IntIUVSerializer : IUVSerializer {
    override val serializer: KSerializer<*>
        get() = IntSerializer
}

object UnitIUVSerializer : IUVSerializer {
    override val serializer: KSerializer<*>
        get() = UnitSerializer
}

object StringIUVSerializer : IUVSerializer {
    override val serializer: KSerializer<*>
        get() = StringSerializer
}