package com.example.bluepear.data.adapters

import com.example.bluepear.ui.canvas.Layer
import com.example.bluepear.opengl.Line
import com.google.gson.*
import java.lang.reflect.Type

class LayerTypeAdapter : JsonSerializer<Layer>, JsonDeserializer<Layer> {

    override fun serialize(src: Layer, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.add("id", JsonPrimitive(src.id))
        jsonObject.add("isVisible", JsonPrimitive(src.isVisible))
        
        val linesArray = JsonArray()
        for (line in src.lines) {
            linesArray.add(context.serialize(line))
        }
        jsonObject.add("lines", linesArray)

        return jsonObject
    }


    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Layer {
        val jsonObject = json.asJsonObject
        val layer = Layer(
            id = jsonObject.getAsJsonPrimitive("id").asInt,
            isVisible = jsonObject.getAsJsonPrimitive("isVisible").asBoolean,
        )

        val linesArray = jsonObject.getAsJsonArray("lines")
        linesArray.forEach { lineJson ->
            val line = context.deserialize<Line>(lineJson, Line::class.java)
            layer.lines.add(line)
        }

        return layer
    }
}

