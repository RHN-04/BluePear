package com.example.bluepear.data.adapters

import com.example.bluepear.data.Work
import com.example.bluepear.opengl.Line
import com.example.bluepear.ui.canvas.Layer
import com.google.gson.*
import java.lang.reflect.Type
import java.nio.FloatBuffer

class WorkTypeAdapter : JsonSerializer<Work>, JsonDeserializer<Work> {

    override fun serialize(src: Work, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val gson = GsonBuilder()
            .registerTypeAdapter(FloatBuffer::class.java, FloatBufferAdapter())
            .create()

        val jsonObject = JsonObject()
        jsonObject.add("title", JsonPrimitive(src.title))

        val layersArray = JsonArray()
        src.layers.forEach { layersArray.add(gson.toJsonTree(it, Layer::class.java)) }
        jsonObject.add("layers", layersArray)

        val linesArray = JsonArray()
        src.lines.forEach { linesArray.add(gson.toJsonTree(it, Line::class.java)) }
        jsonObject.add("lines", linesArray)

        return jsonObject
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Work {
        val gson = GsonBuilder()
            .registerTypeAdapter(FloatBuffer::class.java, FloatBufferAdapter())
            .create()

        val jsonObject = json.asJsonObject
        val work = Work(
            title = jsonObject.getAsJsonPrimitive("title").asString
        )

        val layersArray = jsonObject.getAsJsonArray("layers")
        layersArray.forEach { layerJson ->
            val layer = gson.fromJson(layerJson, Layer::class.java)
            work.layers.add(layer)
        }

        val linesArray = jsonObject.getAsJsonArray("lines")
        linesArray.forEach { lineJson ->
            val line = gson.fromJson(lineJson, Line::class.java)
            work.lines.add(line)
        }

        return work
    }
}
