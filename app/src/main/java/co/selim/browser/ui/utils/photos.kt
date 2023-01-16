package co.selim.browser.ui.utils

import co.selim.browser.model.Photo

val Photo.src: String
    get() = "https://selim.co/photos/$uri"

val Photo.thumbnailSrc: String
    get() = "https://selim.co/photos/$uri?thumbnail=true"
