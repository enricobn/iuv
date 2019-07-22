package org.iuv.shared

import kotlinx.serialization.Serializable
import org.iuv.shared.utils.Either
import org.iuv.shared.utils.Left
import org.iuv.shared.utils.Right

@Serializable
// TODO use directly an Either? Look at https://github.com/Kotlin/kotlinx.serialization/issues/103
data class IUVWebSocketMessage(val id: String, private val message: String?, private val error: String?) {

    fun toEither() : Either<String, String> =
        if (error != null) {
            Left(error)
        } else {
            Right(message!!)
        }

}