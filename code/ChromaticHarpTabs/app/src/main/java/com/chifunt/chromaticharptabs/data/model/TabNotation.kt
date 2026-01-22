package com.chifunt.chromaticharptabs.data.model

import org.json.JSONArray
import org.json.JSONObject

data class TabNote(
    val hole: Int,
    val isBlow: Boolean,
    val isSlide: Boolean
)

data class TabNotation(
    val lines: List<List<TabNote>>
)

object TabNotationJson {

    fun fromJson(content: String): TabNotation? {
        if (content.isBlank()) {
            return null
        }
        return runCatching {
            val root = JSONObject(content)
            val linesArray = root.getJSONArray(JSON_LINES)
            val lines = buildList {
                for (i in 0 until linesArray.length()) {
                    val lineArray = linesArray.getJSONArray(i)
                    add(parseLine(lineArray))
                }
            }
            TabNotation(lines = lines)
        }.getOrNull()
    }

    fun toJson(notation: TabNotation): String {
        val linesArray = JSONArray()
        notation.lines.forEach { line ->
            val lineArray = JSONArray()
            line.forEach { note ->
                val noteObject = JSONObject()
                    .put(JSON_HOLE, note.hole)
                    .put(JSON_BLOW, note.isBlow)
                    .put(JSON_SLIDE, note.isSlide)
                lineArray.put(noteObject)
            }
            linesArray.put(lineArray)
        }
        val root = JSONObject()
            .put(JSON_VERSION, 1)
            .put(JSON_LINES, linesArray)
        return root.toString()
    }

    private fun parseLine(lineArray: JSONArray): List<TabNote> {
        return buildList {
            for (i in 0 until lineArray.length()) {
                val noteObject = lineArray.getJSONObject(i)
                val hole = noteObject.getInt(JSON_HOLE)
                val isBlow = noteObject.optBoolean(JSON_BLOW, true)
                val isSlide = noteObject.optBoolean(JSON_SLIDE, false)
                add(TabNote(hole = hole, isBlow = isBlow, isSlide = isSlide))
            }
        }
    }

    private const val JSON_VERSION = "version"
    private const val JSON_LINES = "lines"
    private const val JSON_HOLE = "hole"
    private const val JSON_BLOW = "blow"
    private const val JSON_SLIDE = "slide"
}
