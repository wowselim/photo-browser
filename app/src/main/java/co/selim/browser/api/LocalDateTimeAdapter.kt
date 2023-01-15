package co.selim.browser.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.time.LocalDateTime

object LocalDateTimeAdapter : JsonAdapter<LocalDateTime>() {
    override fun fromJson(reader: JsonReader): LocalDateTime? {
        val text = reader.nextString()
        return LocalDateTime.parse(text)
    }

    override fun toJson(writer: JsonWriter, value: LocalDateTime?) {
        writer.value(value?.toString())
    }
}
