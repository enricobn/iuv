package org.iuv.spring

import kotlinx.serialization.KSerializer

interface IUVSerializer<T> {
    val serializer: KSerializer<T>
}