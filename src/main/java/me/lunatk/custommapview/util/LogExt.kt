package me.lunatk.custommapview.util

import android.util.Log
import me.lunatk.custommapview.BuildConfig

fun debug(block: () -> Unit) {
    if (BuildConfig.DEBUG) {
        block.invoke()
    }
}

fun<T: Any> T.logi(msg: String) {
    debug {
        Log.i(javaClass.simpleName, msg)
    }
}

fun<T: Any> T.logd(msg: String) {
    debug {
        Log.d(javaClass.simpleName, msg)
    }
}