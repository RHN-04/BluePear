package com.example.bluepear.data.adapters

import com.google.gson.*
import java.lang.reflect.Type
import java.nio.FloatBuffer

class FloatBufferAdapter : JsonDeserializer<FloatBuffer>, JsonSerializer<FloatBuffer> {
    override fun serialize(src: FloatBuffer?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        if (src == null) return JsonArray()
        val array = FloatArray(src.remaining())
        src.get(array)
        src.rewind()
        return JsonArray().apply {
            array.forEach { add(it) }
        }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): FloatBuffer {
        if (json == null || !json.isJsonArray) return FloatBuffer.allocate(0)
        val array = json.asJsonArray.map { it.asFloat }.toFloatArray()
        val buffer = FloatBuffer.allocate(array.size)
        buffer.put(array)
        buffer.rewind()
        return buffer
    }
}
