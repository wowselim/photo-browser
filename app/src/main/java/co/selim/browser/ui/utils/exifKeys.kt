package co.selim.browser.ui.utils

import co.selim.browser.R
import co.selim.browser.model.ExifKey
import co.selim.browser.model.ExifKey.*
import com.ioki.textref.TextRef

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
