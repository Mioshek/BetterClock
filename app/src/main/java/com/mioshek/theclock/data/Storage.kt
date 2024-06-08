package com.mioshek.theclock.data

import java.util.concurrent.ConcurrentHashMap

object Storage {
    private val objects: MutableMap<String, Any> = ConcurrentHashMap()

    fun put(key: String?, value: Any?) {
        if (key == null || value == null) {
            throw NullPointerException("Key and/or Value cannot be null")
        }
        objects[key] = value
    }

    fun <V> take(key: String): V {
        if (objects[key] != null){
            return objects.remove(key) as V
        }
        else throw NullPointerException("Key and/or Value cannot be null")
    }

    fun size(): Int {
        return objects.size
    }

    fun clear() {
        objects.clear()
    }
}