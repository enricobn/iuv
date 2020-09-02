package org.iuv.core.html.enums

enum class TrackKind(val value: String) {
        subtitles("subtitles"),
        captions("captions"),
        descriptions("descriptions"),
        chapters("chapters"),
        metadata("metadata"),
    ;
     companion object {
            fun fromValue(value: String): TrackKind = values().first { it.value == value }
    }
}