package com.example.scan.wifi

import WifiScanner
import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scan.R
import com.example.scan.bluetooth.BluetoothDeviceAdapter
import com.example.scan.bluetooth.BluetoothManager
import com.example.scan.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding
    private lateinit var wifiScanner: WifiScanner
    private var wifiManager: WifiManager? = null
    private lateinit var scanResultAdapter: ScanResultAdapter


    // blu
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothDeviceAdapter: BluetoothDeviceAdapter

    @RequiresApi(Build.VERSION_CODES.Q) override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, filter)
        wifiScanner = WifiScanner(this)
        binding.scan.setOnClickListener {
            startScanningWithPermissionCheck()

            //blu
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val packageName = packageName
                val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
                if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                    bluetoothManager.requestIgnoreBatteryOptimizations()
                } else {
                    bluetoothManager.startScanning()
                }
            } else {
                bluetoothManager.startScanning()
            }


        }


        binding.recycler.layoutManager = LinearLayoutManager(this)


        //blu
        bluetoothManager = BluetoothManager(this)
        binding.recyclerBlu.layoutManager = LinearLayoutManager(this)
        bluetoothDeviceAdapter = BluetoothDeviceAdapter(emptyList())
        binding.recyclerBlu.adapter = bluetoothDeviceAdapter


    }

    private val wifiScanReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?)
        {
            val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
            if (success)
            {
                val scanResults = wifiManager?.scanResults
                if (scanResults != null)
                {
                    processScanResults(scanResults)
                }
            }
        }
    }

    private fun processScanResults(scanResults: List<ScanResult>)
    {


        scanResultAdapter = ScanResultAdapter(scanResults)
        binding.recycler.adapter = scanResultAdapter
        scanResultAdapter.notifyDataSetChanged()
        binding.wifiSize.text = scanResults.size.toString()


    }

//
//    override fun onResume()
//    {
//        super.onResume()
//        if (checkPermission())
//        {
//            wifiScanner.startScanning()
//        }
//    }

    private fun startScanningWithPermissionCheck()
    {
        if (checkPermission())
        {
            wifiScanner.startScanning()
        } else
        {
            requestPermission()
        }

    }

    private fun checkPermission(): Boolean
    {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission()
    {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE)
        {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                wifiScanner.startScanning()
            } else
            {
                // Handle permission denial or cancellation
                // You may display a message or take appropriate action here
                Toast.makeText(this@MainActivity, "Please grant the permission to be able to scan ", Toast.LENGTH_LONG).show()
                startScanningWithPermissionCheck()
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) {
            if (resultCode == Activity.RESULT_OK) {
               bluetoothManager.startScanning()
            } else {
                // Permission denied or not available
                // Handle accordingly
            }
        }
    }


    fun updateDeviceList(devices: List<BluetoothDevice>) {
        bluetoothDeviceAdapter.devices = devices
        bluetoothDeviceAdapter.notifyDataSetChanged()
        binding.blueSize.text =devices.size.toString()
    }
    companion object
    {
        private const val REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 1001
        private const val PERMISSION_REQUEST_CODE = 123
    }
}
