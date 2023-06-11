package com.example.scan.bluetooth

import WifiScanner.Companion.TAG
import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.scan.wifi.MainActivity

class BluetoothManager(private val activity: Activity) {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothReceiver: BroadcastReceiver
    private val bluetoothDevices: MutableList<BluetoothDevice> = mutableListOf()

    var counter_wifi = 0




    @RequiresApi(Build.VERSION_CODES.Q) fun startScanning() {


        startDeviceDiscovery()
    }

    fun stopScanning() {
        bluetoothAdapter?.cancelDiscovery()
    }
    init {
        bluetoothReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.R) override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        device?.let {
                            // Do something with the discovered device
                            Log.d(TAG, "Found device Bluetooth: ${device.alias} (${device.address})")
                            bluetoothDevices.add(device)
                            updateDeviceList(bluetoothDevices)




                        }
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        Log.d(TAG, "Discovery finished. Found ${bluetoothDevices.size} Blu devices.")
                        Toast.makeText(context, "Discovery finished. Found ${bluetoothDevices.size} Blu devices.", Toast.LENGTH_LONG    ).show()
//


                    }
                }
            }
        }
    }


    private fun updateDeviceList(devices: List<BluetoothDevice>) {
        activity.runOnUiThread {
            (activity as MainActivity).updateDeviceList(devices)
        }
    }
    @RequiresApi(Build.VERSION_CODES.S) fun startDeviceDiscovery() {

        if (!hasBluetoothPermissions()) {
            requestBluetoothPermissions()
            return
        }

        if (bluetoothAdapter == null) {
            // Bluetooth is not supported on this device
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH)

        } else {
            registerBluetoothReceiver()
            bluetoothAdapter.startDiscovery()
            bluetoothDevices.clear()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S) private fun hasBluetoothPermissions(): Boolean {
        return activity.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
        activity.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
        activity.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
        activity.checkSelfPermission(Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED &&
                activity.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.S) private fun requestBluetoothPermissions() {
        val permissions = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.WAKE_LOCK
        )
        activity.requestPermissions(permissions, REQUEST_BLUETOOTH_PERMISSIONS)
    }

    private fun registerBluetoothReceiver() {
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        activity.registerReceiver(bluetoothReceiver, filter)
    }

    fun unregisterBluetoothReceiver() {
        activity.unregisterReceiver(bluetoothReceiver)
    }

    private val REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 1001

    fun requestIgnoreBatteryOptimizations() {
        val packageName = activity.packageName
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.parse("package:$packageName")
        activity.startActivityForResult(intent, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
    }
    companion object {
        private const val REQUEST_ENABLE_BLUETOOTH = 1
        private const val REQUEST_BLUETOOTH_PERMISSIONS = 2
    }
}
