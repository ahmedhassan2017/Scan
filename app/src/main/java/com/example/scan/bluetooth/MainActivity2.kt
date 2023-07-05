package com.example.scan.bluetooth

import BluetoothScanner
import WifiScanner
import WifiScanner2
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.scan.R
import com.example.scan.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity()
{
    private lateinit var binding: ActivityMain2Binding

    private lateinit var wifiScanner: WifiScanner2
    private lateinit var bluetoothScanner: BluetoothScanner
    private val handler = Handler()
    var wifiSessionCounter = 0
    var bluetoothSessionCounter = 0
    var session_counter = 0

    // BLE
    private lateinit var bleDeviceScanner: BLEDeviceScanner


    private val timerRunnable = object : Runnable
    {
        override fun run()
        {
            val counterWifi = wifiScanner.getCounterWifi()
            val counterBluetooth = bluetoothScanner.getCounter()
            Log.d("MainActivityCounter", "(${session_counter+1}) Wi-Fi: $counterWifi, Bluetooth: $counterBluetooth")
            binding.textview1.append("${session_counter+1}) Wi-Fi: $counterWifi, Bluetooth: $counterBluetooth \n")
            session_counter++
            wifiSessionCounter += counterWifi   // add all wifi counters
            bluetoothSessionCounter += counterBluetooth  // add all blu counters
            wifiScanner.setCounter(0) // Reset the Wi-Fi counter
            bluetoothScanner.setCounter(0) // Reset the Bluetooth counter

            if (session_counter==36)
            {
                Log.d("MainActivityCounter", " Session Ended: Wifi : $wifiSessionCounter , Blu : $bluetoothSessionCounter")
                binding.textview1.append(" Session Ended: Wifi : $wifiSessionCounter , Blu : $bluetoothSessionCounter \n")
                session_counter=0
                wifiSessionCounter =0
                bluetoothSessionCounter =0

            }

            wifiScanner.startScanning()
            bluetoothScanner.startScanning()



            handler.postDelayed(this, 7500) // Repeat every 7.5 seconds

        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main2)
        wifiScanner = WifiScanner2(this@MainActivity2)
        bluetoothScanner = BluetoothScanner(this)


        binding.loopedScan.setOnClickListener{
            wifiScanner.startScanning()
            bluetoothScanner.startScanning()
            handler.postDelayed(timerRunnable, 7500)
        }





        // Initialize BLE device scanner
        bleDeviceScanner = BLEDeviceScanner(this@MainActivity2)

        // Set click listener for the "Start Scan" button

        binding.startBLE.setOnClickListener {
            bleDeviceScanner.startScan()
        }
        binding.stopBLE.setOnClickListener {
            bleDeviceScanner.stopScan()
            wifiScanner.stopScanning()
            bluetoothScanner.stopScanning()

            handler.removeCallbacks(timerRunnable)

        }


        binding.goToNext.setOnClickListener {
            startActivity(Intent(this@MainActivity2,BleActivity::class.java))
        }
    }

    override fun onResume()
    {
        super.onResume()

    }

    override fun onPause()
    {
        super.onPause()
//        wifiScanner.stopScanning()
//        bluetoothScanner.stopScanning()
//        handler.removeCallbacks(timerRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Stop scanning and release resources when the activity is destroyed
        bleDeviceScanner.stopScan()
    }
}

