package space.jtsalva.convey

import android.content.Intent
import android.util.Log

enum class Action(private val action: String) {
    COUNTDOWN_SEARCH("COUNTDOWN_SEARCH"),
    DEVICE_CONNECTED("DEVICE_CONNECTED");

    private companion object {
        const val TAG = "Action"
        const val prefix = "space.jtsalva.convey."
    }

    fun intent(): Intent {
        Log.d(TAG, "Intent created ${this.toString()}")
        return Intent(this.toString())
    }

    override fun toString(): String {
        return prefix + action
    }
}