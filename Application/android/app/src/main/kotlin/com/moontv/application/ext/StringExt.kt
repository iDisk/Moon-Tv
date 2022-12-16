package com.moontv.application.ext

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.moontv.application.model.Season
import java.lang.reflect.Type



inline fun <reified T> String.convertToListObject(): List<T>? {
    val listType: Type = object : TypeToken<List<T?>?>() {}.type
    return Gson().fromJson<List<T>>(this, listType)
}