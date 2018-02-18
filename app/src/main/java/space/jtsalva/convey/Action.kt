package space.jtsalva.convey

import android.content.Intent

enum class Action(private val action: String) {
    COUNTDOWN_SEARCH("COUNTDOWN_SEARCH"),
    DEVICE_CONNECTED("DEVICE_CONNECTED");

    private companion object {
        const val prefix = "space.jtsalva.convey."
    }

    fun intent(): Intent {
        return Intent(action)
    }

    override fun toString(): String {
        return prefix + action
    }
}