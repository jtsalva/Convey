package space.jtsalva.convey

import android.app.Activity
import android.os.Bundle

import android.content.Intent
import android.util.Log
import android.widget.Button

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket

import android.os.Handler

import java.util.UUID

import java.io.DataInputStream

class MainActivity : Activity() {

    private companion object {
        const val APP_NAME  = "Convey"
        const val TAG = "MainActivity"
        const val REQUEST_DISCOVERABLE_BT = 1
        const val SERVICE_UUID = "a0efb116-de0f-4e48-98c1-bd1e21cc64f4"

        // 5 minutes
        const val DISCOVERABLE_DURATION = 300
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request bluetooth discovery
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                .putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION)
        startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_DISCOVERABLE_BT -> {
                when (resultCode) {
                    DISCOVERABLE_DURATION -> {
                        Log.d(TAG, "Set discoverable")
                        startService()
                    }
                    else -> {
                        Log.d(TAG, "Failed to set discoverable")
                        finishAffinity()
                    }
                }
            }
        }
    }

    private fun startService() {
        Log.d(TAG, "Starting service")
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        val appUUID = UUID.fromString(SERVICE_UUID)
        val serverSocket: BluetoothServerSocket = bluetoothAdapter
                .listenUsingInsecureRfcommWithServiceRecord(APP_NAME, appUUID)

        var bluetoothSocket: BluetoothSocket? = null
        try {
            bluetoothSocket = serverSocket.accept()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }

        serverSocket.close()
        bluetoothAdapter.cancelDiscovery()

        val searchButton = findViewById<Button>(R.id.search_button)
        val inputStream = DataInputStream(bluetoothSocket?.inputStream)
        val handler = Handler()

        object : Thread() {
            override fun run() {
                Log.d(TAG, "Socket connection thread started")
                super.run()
                val buffer = ByteArray(256)
                var bytes: Int?
                var readMessage: String?
                try {
                    while (true) {
                        bytes = inputStream.read(buffer)
                        readMessage = String(buffer, 0, bytes)
                        Log.d(TAG, readMessage)
                        handler.post({ searchButton.text = readMessage })
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                    handler.post({ searchButton.text = "Disconnected" })
                }
            }
        }.start()
    }
}
