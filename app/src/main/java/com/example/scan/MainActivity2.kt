package com.example.scan
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.scan.databinding.ActivityMain2Binding
import com.example.scan.wifi_direct.WifiDirectManager
import com.example.scan.wifi_direct.WifiDirectManager.Companion.TAG

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private lateinit var wifiDirectManager: WifiDirectManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main2)


        wifiDirectManager = WifiDirectManager(this)

        binding.textview2.setOnClickListener {
            startDeviceDiscoveryWithPermissionCheck()
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkPermission()) {
            wifiDirectManager.registerReceiver()
        }
        else Toast.makeText(this@MainActivity2, "permission", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
//        wifiDirectManager.unregisterReceiver()
    }

    private fun startDeviceDiscoveryWithPermissionCheck() {
        if (checkPermission()) {
            wifiDirectManager.discoverPeers()
        } else {
            requestPermission()
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                wifiDirectManager.discoverPeers()
            } else {
                // Handle permission denial or cancellation
                // You may display a message or take appropriate action here
                Log.i(TAG, "onRequestPermissionsResult: failed")

            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }


}
