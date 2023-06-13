import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

class BluetoothScanner(private val context: Context) {
    private var counterBluetooth = 0
    private var bluetoothAdapter: BluetoothAdapter? = null
    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {

                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    Log.d("BluetoothScanner", "Normal Bluetooth ->${device?.name} ${device?.address}")

                    device?.let {
                        counterBluetooth++
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d("BluetoothScanner", "Bluetooth discovery finished")
                }
            }
        }
    }

    init {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    }

    fun startScanning() {
        context.registerReceiver(
            bluetoothReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )
        context.registerReceiver(
            bluetoothReceiver,
            IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        )
        bluetoothAdapter?.startDiscovery()
    }

    fun stopScanning() {
        context.unregisterReceiver(bluetoothReceiver)
        bluetoothAdapter?.cancelDiscovery()
    }

    fun getCounter(): Int {
        return counterBluetooth
    }

    fun setCounter(value: Int) {
        counterBluetooth = value
    }
}