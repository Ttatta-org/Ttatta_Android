package com.umc.data.implementation.preference

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.umc.core.model.EquippedItem
import com.umc.data.preference.ItemPreference
import androidx.core.content.edit

class ItemPreferenceImpl(context: Context) : ItemPreference {
    companion object {
        private const val PREF_NAME = "item"
        private const val ITEM_LIST_KET = "item_list"
    }

    private val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val type = object : TypeToken<List<EquippedItem>>() {}.type
    private val gson = Gson()

    override var itemList: List<EquippedItem>
        get() = pref
            .getString(ITEM_LIST_KET, null)
            ?.runCatching { gson.fromJson<List<EquippedItem>>(this, type) }
            ?.getOrNull() ?: emptyList()
        set(value) {
            pref.edit { putString(ITEM_LIST_KET, gson.toJson(value)) }
        }
}