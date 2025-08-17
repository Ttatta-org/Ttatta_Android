// com.umc.data.serializer.LocalTimeSerializer.kt
package com.umc.data.serializer

import com.google.gson.*
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.lang.reflect.Type
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LocalTimeSerializer :
    JsonDeserializer<LocalTime>,
    JsonSerializer<LocalTime>,
    JsonAdapter<LocalTime>() {

    private val formatter = DateTimeFormatter.ISO_LOCAL_TIME // "HH:mm:ss"

    // Gson
    override fun deserialize(json: JsonElement, typeOfT: Type, ctx: JsonDeserializationContext): LocalTime =
        LocalTime.parse(json.asString, formatter)

    override fun serialize(src: LocalTime, typeOfSrc: Type, ctx: JsonSerializationContext): JsonElement =
        JsonPrimitive(src.format(formatter))

    // Moshi
    @FromJson
    override fun fromJson(reader: JsonReader): LocalTime? =
        LocalTime.parse(reader.nextString(), formatter)

    @ToJson
    override fun toJson(writer: JsonWriter, value: LocalTime?) {
        writer.value(value?.format(formatter))
    }
}
