package space.jtsalva.convey

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.io.DataInputStream
import java.util.UUID


class ConveyService : Service() {

    private companion object {
        const val TAG = "ConveyService"
        const val SERVICE_UUID = "a0efb116-de0f-4e48-98c1-bd1e21cc64f4"
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startBTStrean()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun startBTStrean() {
        Log.i(TAG, "Starting BT stream")
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        val appUUID = UUID.fromString(SERVICE_UUID)
        val serverSocket: BluetoothServerSocket = bluetoothAdapter
                .listenUsingInsecureRfcommWithServiceRecord(TAG, appUUID)

//        var bluetoothSocket: BluetoothSocket? = null
//        val handler = Handler()

        object : Thread() {
            override fun run() {
                super.run()
                try {
                    Log.i(TAG, "Service accepting UUID : $SERVICE_UUID")
                    openDataStrean(serverSocket.accept())

                    serverSocket.close()
                    bluetoothAdapter.cancelDiscovery()

                    sendBroadcast(Action.DEVICE_CONNECTED.intent())
                } catch (e: Exception) {
                    Log.e(TAG, e.message)
                }
            }
        }.start()
    }

    private fun openDataStrean(bluetoothSocket: BluetoothSocket) {
        Log.i(TAG, "Opening data stream")

        val inputStream = DataInputStream(bluetoothSocket.inputStream)

        object : Thread() {
            override fun run() {
                Log.d(TAG, "data stream thread opened")
                super.run()
                val buffer = ByteArray(256)
                var bytes: Int?
                var readMessage: String?
                try {
                    while (true) {
                        bytes = inputStream.read(buffer)
                        readMessage = String(buffer, 0, bytes)
                        Log.d(TAG, readMessage)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.message)
                }
            }
        }.start()
    }

}