package com.example.scan.bluetooth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.scan.R
import com.example.scan.databinding.ActivityBleBinding
import com.example.scan.databinding.ActivityMainBinding

class BleActivity : AppCompatActivity()
{
    private var scanStarted: Boolean = false
    private lateinit var binding: ActivityBleBinding
    private lateinit var bleDeviceScanner: BLEDeviceScanner
    private val scanInterval = 7500L // Scan interval in milliseconds
    private val handler = Handler()


    private val scanRunnable = object : Runnable
    {
        override fun run()
        {

            // Get the scanned devices and log their count and content
            val scannedDevices = bleDeviceScanner.getScannedDevices()
            if (scannedDevices.isNotEmpty())
            {
                Log.d("BleActivityScan", "Total devices found: ${scannedDevices.size}")
                for (device in scannedDevices)
                {
                    Log.d("BleActivityScan", "Device: ${device.name} (${device.address})")
                }
            }

            // Clear any previous scanned devices
            bleDeviceScanner.clearScannedDevices()
            bleDeviceScanner.startScan()



            handler.postDelayed(this, scanInterval)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ble)

        bleDeviceScanner = BLEDeviceScanner(this@BleActivity)

        // Set click listener for the "Start Scan" button
        binding.startBLEScan.setOnClickListener {

            if (!scanStarted) startScanning()
            else Toast.makeText(this@BleActivity, "Already Started", Toast.LENGTH_SHORT).show()
        }

        // Set click listener for the "Stop Scan" button
        binding.stopBLEScan.setOnClickListener {
            stopScanning()
        }


    }

    private fun startScanning()
    {

        scanStarted = true
        // Clear any previous scanned devices
        bleDeviceScanner.clearScannedDevices()

        // Start scanning
        handler.post(scanRunnable)
    }

    private fun stopScanning()
    {
        Toast.makeText(this@BleActivity, "Stopped", Toast.LENGTH_SHORT).show()

        scanStarted = false
        handler.removeCallbacks(scanRunnable)
        bleDeviceScanner.stopScan()


    }

    override fun onDestroy()
    {
        super.onDestroy()
        // Stop scanning and clear the scanned devices when the activity is destroyed
        stopScanning()
        bleDeviceScanner.clearScannedDevices()
    }
}