package com.example.publichealth.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharePref(prefContext: Context) {
    private val queuePref = "Queue"
    private var context = prefContext
    private val pref: SharedPreferences = context.getSharedPreferences(queuePref,Context.MODE_PRIVATE)

    fun addQueueStack(queue: Int){
        pref.edit {
            putInt("currentQueue",queue)
            apply()
        }
    }

    fun getQueueStack(): Int {
        return pref.getInt("currentQueue",0)
    }

    fun clearQueueStack(){
        pref.edit().clear().apply()
    }
}