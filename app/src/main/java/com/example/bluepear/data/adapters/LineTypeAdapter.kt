package com.example.bluepear.data.adapters

import com.example.bluepear.opengl.Line
import com.google.gson.*
import java.lang.reflect.Type

class LineTypeAdapter : JsonSerializer<Line>, JsonDeserializer<Line> {

    override fun serialize(src: Line, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()

        val colorArray = JsonArray().apply {
            src.getColor.forEach { add(it) }
        }
        jsonObject.add("color", colorArray)

        jsonObject.add("width", JsonPrimitive(src.getWidth))

        val pointsArray = JsonArray().apply {
            src.getPoints.forEach { add(it) }
        }
        jsonObject.add("points", pointsArray)

        jsonObject.add("isComplete", JsonPrimitive(src.isComplete))

        jsonObject.add("layerId", JsonPrimitive(src.layerId))

        return jsonObject
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Line {
        val jsonObject = json.asJsonObject

        val colorArray = jsonObject.getAsJsonArray("color").map { it.asFloat }
        val pointsArray = jsonObject.getAsJsonArray("points").map { it.asFloat }

        val line = Line(colorArray.toFloatArray(), jsonObject.getAsJsonPrimitive("width").asFloat,
            jsonObject.getAsJsonPrimitive("layerId").asInt)
        line.getPoints.addAll(pointsArray)
        line.isComplete = jsonObject.getAsJsonPrimitive("isComplete").asBoolean
        line.complete()

        return line
    }
}
