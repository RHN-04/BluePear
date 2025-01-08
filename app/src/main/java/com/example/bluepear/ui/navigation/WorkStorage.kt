package com.example.bluepear.ui.navigation

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.example.bluepear.data.Work
import com.example.bluepear.data.adapters.FloatBufferAdapter
import com.example.bluepear.data.adapters.LayerTypeAdapter
import com.example.bluepear.data.adapters.LineTypeAdapter
import com.example.bluepear.data.adapters.WorkTypeAdapter
import com.example.bluepear.opengl.Line
import com.example.bluepear.ui.canvas.Layer
import java.nio.FloatBuffer

class WorkStorage(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("work_prefs", Context.MODE_PRIVATE)
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Work::class.java, WorkTypeAdapter())
        .registerTypeAdapter(Layer::class.java, LayerTypeAdapter())
        .registerTypeAdapter(Line::class.java, LineTypeAdapter())
        .registerTypeAdapter(FloatBuffer::class.java, FloatBufferAdapter())
        .create()

    fun saveWork(work: Work) {
        val workJson = gson.toJson(work)
        println("Saving Work: $workJson")
        sharedPreferences.edit().putString(work.title, workJson).apply()
    }

    fun loadWork(title: String): Work? {
        val workJson = sharedPreferences.getString(title, null) ?: return null
        println("Loading Work: $workJson")
        return gson.fromJson(workJson, Work::class.java)
    }

    fun loadAllWorks(): List<Work> {
        val allWorks = mutableListOf<Work>()
        val keys = sharedPreferences.all.keys
        for (key in keys) {
            val workJson = sharedPreferences.getString(key, null)
            if (workJson != null) {
                val work = gson.fromJson(workJson, Work::class.java)
                allWorks.add(work)
            }
        }
        return allWorks
    }
}
