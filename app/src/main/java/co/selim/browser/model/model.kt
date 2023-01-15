package co.selim.browser.model

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class AlbumPage(
    val albums: List<Album>,
    val index: Int,
    val next: Int?,
    val previous: Int?,
)

@JsonClass(generateAdapter = true)
data class Album(
    val id: String,
    val slug: String,
    val title: String,
    val coverPhoto: Photo,
    val photos: List<Photo>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

@JsonClass(generateAdapter = true)
data class Photo(
    val uri: String,
    val contentType: String,
    val exifData: Map<ExifKey, String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

enum class ExifKey {
    APERTURE,
    DATE_TIME,
    FLASH_STATUS,
    FOCAL_LENGTH,
    ISO,
    LENS,
    MAKE,
    MODEL,
    SHUTTER_SPEED,
}
