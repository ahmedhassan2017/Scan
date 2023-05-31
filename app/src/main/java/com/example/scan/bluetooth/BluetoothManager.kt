package com.example.scan.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.scan.wifi_direct.WifiDirectManager.Companion.TAG

class BluetoothManager(private val activity: Activity) {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothReceiver: BroadcastReceiver
    private val bluetoothDevices: MutableList<BluetoothDevice> = mutableListOf()

    init {
        bluetoothReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.R) override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        device?.let {
                            // Do something with the discovered device
                            Log.d(TAG, "Found device: ${device.alias} (${device.address})")
                            bluetoothDevices.add(device)


                        }
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        Log.d(TAG, "Discovery finished")
                        Log.d(TAG, "Discovery finished. Found ${bluetoothDevices.size} Blu devices.")

                    }
                }
            }
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
        }
    }

    @RequiresApi(Build.VERSION_CODES.S) private fun hasBluetoothPermissions(): Boolean {
        return activity.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
        activity.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
        activity.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                activity.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.S) private fun requestBluetoothPermissions() {
        val permissions = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
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

    companion object {
        private const val REQUEST_ENABLE_BLUETOOTH = 1
        private const val REQUEST_BLUETOOTH_PERMISSIONS = 2
    }
}
