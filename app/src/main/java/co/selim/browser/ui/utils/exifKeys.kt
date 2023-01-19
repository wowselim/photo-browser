package co.selim.browser.ui.utils

import co.selim.browser.R
import co.selim.browser.model.ExifKey
import co.selim.browser.model.ExifKey.*
import com.ioki.textref.TextRef
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val ExifKey.displayText: TextRef
    get() = when (this) {
        APERTURE -> TextRef.stringRes(R.string.aperture)
        DATE_TIME -> TextRef.stringRes(R.string.date_time)
        FLASH_STATUS -> TextRef.stringRes(R.string.flash_status)
        FOCAL_LENGTH -> TextRef.stringRes(R.string.focal_length)
        ISO -> TextRef.stringRes(R.string.iso)
        LENS -> TextRef.stringRes(R.string.lens)
        MAKE -> TextRef.stringRes(R.string.make)
        MODEL -> TextRef.stringRes(R.string.model)
        SHUTTER_SPEED -> TextRef.stringRes(R.string.shutter_speed)
    }


fun ExifKey.formatValue(value: String): String {
    return when (this) {
        APERTURE -> "F$value"
        SHUTTER_SPEED -> "${value}s"
        DATE_TIME -> parseDateTimeOrNull(value) ?: value
        FOCAL_LENGTH,
        FLASH_STATUS,
        ISO,
        LENS,
        MAKE,
        MODEL -> value
    }
}

private val inputDateFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm")
private val outputDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
private fun parseDateTimeOrNull(value: String): String? {
    return try {
        val dateTimeSubstring = value.slice(0..15)
        val dateTime = LocalDateTime.parse(dateTimeSubstring, inputDateFormatter)
        outputDateFormatter.format(dateTime)
    } catch (e: DateTimeException) {
        null
    }
}
