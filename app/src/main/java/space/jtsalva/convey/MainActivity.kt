package space.jtsalva.convey

import android.app.Activity
import android.os.Bundle

import android.util.Log

import android.bluetooth.BluetoothAdapter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

import android.view.View

import kotlinx.android.synthetic.main.activity_main.landing_text
import kotlinx.android.synthetic.main.activity_main.searching_bar
import kotlinx.android.synthetic.main.activity_main.activity_main

class MainActivity : Activity() {

    private companion object {
        const val TAG = "MainActivity"
        const val REQUEST_DISCOVERABLE_BT = 1

        // 5 minutes
        const val DISCOVERABLE_DURATION = 300
    }

    private val receiver = object : BroadcastReceiver() {
        var registered = false

        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Intent ${intent?.action}")
            when (intent?.action) {
                Action.COUNTDOWN_SEARCH.toString() -> {}
                Action.DEVICE_CONNECTED.toString() -> {}
            }
        }

        fun register() {
            this.registered = true
        }

        fun unregister() {
            this.registered = false
        }
    }

    private var conveyService: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        conveyService = Intent(this, ConveyService::class.java)

        // Request bluetooth discovery
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                .putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION)
        startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT)
    }

    override fun onResume() {
        super.onResume()

        if (!receiver.registered) {
            registerReceiver(receiver, IntentFilter(Action.COUNTDOWN_SEARCH.toString()))
            receiver.register()
        }
    }

    override fun onPause() {
        super.onPause()

        if (receiver.registered) {
            unregisterReceiver(receiver)
            receiver.unregister()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_DISCOVERABLE_BT -> {
                when (resultCode) {
                    DISCOVERABLE_DURATION -> {
                        Log.d(TAG, "Set discoverable")
                        activity_main.setOnClickListener { startConveyService() }
                    }
                    else -> {
                        Log.d(TAG, "Failed to set discoverable")
                        finishAffinity()
                    }
                }
            }
        }
    }

    private fun startConveyService() {
        Log.d(TAG, "Starting Convey Service")
        startService(conveyService)

        activity_main.setOnClickListener { stopConveyService() }

        landing_text.visibility = View.INVISIBLE
        searching_bar.visibility = View.VISIBLE
    }

    private fun stopConveyService() {
        Log.d(TAG, "Stopping Convey Service")
        stopService(conveyService)

        activity_main.setOnClickListener { startConveyService() }

        landing_text.visibility = View.VISIBLE
        searching_bar.visibility = View.INVISIBLE
    }

}
