package com.umc.data.implementation.preference

import android.content.Context
import com.umc.data.preference.ItemPreference

class ItemPreferenceImpl(context: Context) : ItemPreference {
    companion object {
        private const val PREF_NAME = "item"
        private const val TTOTTO_HEAD_ITEM_ID_KEY = "ttotto_head_item_id"
        private const val TTOTTO_BODY_ITEM_ID_KEY = "ttotto_body_item_id"
        private const val TTUTTU_HEAD_ITEM_ID_KEY = "ttuttu_head_item_id"
        private const val TTUTTU_BODY_ITEM_ID_KEY = "ttuttu_body_item_id"

    }

    private val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override var ttottoBodyItemId: String?
        get() = pref.getString(TTOTTO_BODY_ITEM_ID_KEY, null)
        set(value) { pref.edit().putString(TTOTTO_BODY_ITEM_ID_KEY, value).apply() }

    override var ttottoHeadItemId: String?
        get() = pref.getString(TTOTTO_HEAD_ITEM_ID_KEY, null)
        set(value) { pref.edit().putString(TTOTTO_HEAD_ITEM_ID_KEY, value).apply() }

    override var ttuttuBodyItemId: String?
        get() = pref.getString(TTUTTU_BODY_ITEM_ID_KEY, null)
        set(value) { pref.edit().putString(TTUTTU_BODY_ITEM_ID_KEY, value).apply() }

    override var ttuttuHeadItemId: String?
        get() = pref.getString(TTUTTU_HEAD_ITEM_ID_KEY, null)
        set(value) { pref.edit().putString(TTUTTU_HEAD_ITEM_ID_KEY, value).apply() }
}