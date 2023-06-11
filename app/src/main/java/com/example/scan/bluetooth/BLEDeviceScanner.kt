package com.example.scan.bluetooth
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.*
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log

class BLEDeviceScanner(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    private val scanCallback: ScanCallback
    private val bleDevices: MutableMap<String, BluetoothDevice> = mutableMapOf()
    private val classicDevices: MutableMap<String, BluetoothDevice> = mutableMapOf()
    private val unknownDevices: MutableMap<String, BluetoothDevice> = mutableMapOf()
    private val dualDevices: MutableMap<String, BluetoothDevice> = mutableMapOf()

    init {
        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device: BluetoothDevice? = result.device

                device?.let {

                    when (device.type) {

                        BluetoothDevice.DEVICE_TYPE_LE -> {
                            if (!bleDevices.containsKey(device.address)) {
                                bleDevices[device.address] = device
                                Log.d("BLEDeviceScanner", "Found BLE device: ${device.name} (${device.address})")
                            }
                        }
                        BluetoothDevice.DEVICE_TYPE_CLASSIC -> {
                            if (!classicDevices.containsKey(device.address)) {
                                classicDevices[device.address] = device
                                Log.d("BLEDeviceScanner", "Found Bluetooth Classic device: ${device.name} (${device.address})")
                            }
                        }
                        BluetoothDevice.DEVICE_TYPE_UNKNOWN -> {
                            if (!unknownDevices.containsKey(device.address)) {
                                unknownDevices[device.address] = device
                                Log.d("BLEDeviceScanner", "Found Unknown device: ${device.name} (${device.address})")
                            }
                        }
                        BluetoothDevice.DEVICE_TYPE_DUAL -> {
                            if (!dualDevices.containsKey(device.address)) {
                                dualDevices[device.address] = device
                                Log.d("BLEDeviceScanner", "Found Dual Mode device: ${device.name} (${device.address})")
                            }
                        }
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e("BLEDeviceScanner", "BLE scan failed with error code: $errorCode")
            }
        }
    }

    fun startScan() {
        if (bluetoothAdapter == null) {
            Log.e("BLEDeviceScanner", "Bluetooth is not supported on this device")
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            Log.e("BLEDeviceScanner", "Bluetooth is not enabled")
            return
        }

        val scannerSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()

        bluetoothLeScanner?.startScan(null, scannerSettings, scanCallback)

        // Stop scanning after a specified duration (e.g., 10 seconds)
        val scanDuration = 10000L
        Handler(Looper.getMainLooper()).postDelayed({
            stopScan()
        }, scanDuration)
    }

    fun stopScan() {
        bluetoothLeScanner?.stopScan(scanCallback)
//        logDeviceCount()
    }

    private fun logDeviceCount() {
        Log.d("BLEDeviceScanner", "Total BLE devices found: ${bleDevices.size}")
        for (device in bleDevices.values) {
            Log.d("BLEDeviceScanner", "BLE Device: ${device.name} (${device.address})")
        }

        Log.d("BLEDeviceScanner", "Total Bluetooth Classic devices found: ${classicDevices.size}")
        for (device in classicDevices.values) {
            Log.d("BLEDeviceScanner", "Bluetooth Classic Device: ${device.name} (${device.address})")
        }
    }
}
