package com.umc.data.serializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeSerializer : JsonDeserializer<LocalDateTime>,
    JsonSerializer<LocalDateTime>,
    JsonAdapter<LocalDateTime>() {

    companion object {
        private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): LocalDateTime {
        return LocalDateTime.parse(json.asString, formatter)
    }

    override fun serialize(
        src: LocalDateTime,
        typeOfSrc: Type,
        context: JsonSerializationContext,
    ): JsonElement {
        return JsonPrimitive(src.format(formatter))
    }

    @FromJson
    override fun fromJson(jsonReader: JsonReader): LocalDateTime? {
        return if (jsonReader.peek() == JsonReader.Token.NULL) {
            jsonReader.nextNull()
        } else {
            LocalDateTime.parse(jsonReader.nextString(), formatter)
        }
    }

    @ToJson
    override fun toJson(jsonWriter: JsonWriter, value: LocalDateTime?) {
        if (value == null) jsonWriter.nullValue()
        else jsonWriter.value(value.format(formatter))
    }
}