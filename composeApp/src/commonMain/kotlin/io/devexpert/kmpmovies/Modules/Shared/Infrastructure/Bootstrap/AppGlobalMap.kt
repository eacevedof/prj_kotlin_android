package io.devexpert.kmpmovies.Modules.Shared.Infrastructure.Bootstrap

object AppGlobalMap {
    private val dictionary: MutableMap<AppKeyEnum, Any?> = mutableMapOf()

    fun set(key: AppKeyEnum, value: Any?) {
        dictionary[key] = value
    }

    fun get(key: AppKeyEnum): Any? = dictionary[key]

    fun getString(key: AppKeyEnum): String = dictionary[key] as? String ?: ""

    fun getInt(key: AppKeyEnum): Int = dictionary[key] as? Int ?: 0

    fun has(key: AppKeyEnum): Boolean = dictionary.containsKey(key)

    fun remove(key: AppKeyEnum) {
        dictionary.remove(key)
    }

    fun clear() {
        dictionary.clear()
    }
}
