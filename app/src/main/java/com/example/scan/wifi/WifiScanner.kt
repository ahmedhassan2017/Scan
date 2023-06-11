import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast

class WifiScanner(private val context: Context) {
    companion object{
        val TAG ="ahmed"
    }
    private var wifiManager: WifiManager? = null

     var counter_wifi = 0


    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
            if (success) {
                val scanResults = wifiManager?.scanResults
                if (scanResults != null) {
                    processScanResults(scanResults)
                }
            }
        }
    }

    fun startScanning() {
        wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager?.let { manager ->
            if (!manager.isWifiEnabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Toast.makeText(context, "Wi-Fi is disabled.", Toast.LENGTH_SHORT).show()
                } else {
                    manager.isWifiEnabled = true
                }
            }

            val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            context.registerReceiver(wifiScanReceiver, filter)

            // Start initial scan
            startScan()


    }
    }

    private fun startScan() {
        wifiManager?.startScan()
    }

    private fun processScanResults(scanResults: List<ScanResult>) {
        // Handle the scan results here
        for (result in scanResults) {
            val ssid = result.SSID
            val bssid = result.BSSID
            val signalStrength = result.level

            // Do whatever you want with the scan result data
//            println("SSID: $ssid, BSSID: $bssid, Signal Strength: $signalStrength")
            Log.d(TAG, "Found device WI-FI: SSID: $ssid, BSSID: $bssid, Signal Strength: $signalStrength")



        }
    }
}
