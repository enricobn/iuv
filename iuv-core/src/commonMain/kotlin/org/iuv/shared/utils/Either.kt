package org.iuv.shared.utils

import org.iuv.shared.Task

/**
 * From https://github.com/adelnizamutdinov/kotlin-either
 * // TODO add dependency
 */
@Suppress("unused")
sealed class Either<out L, out R> {
    companion object {
        operator fun <L, R> invoke(left: L, right: R?) =
            if (right == null) Left(left) else Right(right)
    }
}

data class Left<out T>(val value: T) : Either<T, Nothing>()
data class Right<out T>(val value: T) : Either<Nothing, T>()

inline fun <L, R, T> Either<L, R>.fold(left: (L) -> T, right: (R) -> T): T =
    when (this) {
        is Left -> left(value)
        is Right -> right(value)
    }

inline fun <L, R, T> Either<L, R>.flatMap(f: (R) -> Either<L, T>): Either<L, T> =
    fold({ this as Left }, f)

inline fun <L, R, T> Either<L, R>.map(f: (R) -> T): Either<L, T> =
    flatMap { Right(f(it)) }

fun <L, R> Either<L, R>.toTask(): Task<L, R> =
    Task { onFailure, onSuccess ->
        fold(onFailure, onSuccess)
    }