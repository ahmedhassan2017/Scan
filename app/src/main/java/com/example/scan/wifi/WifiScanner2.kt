import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log

class WifiScanner2(private val context: Context) {
    private var counterWifi = 0
    private var wifiManager: WifiManager? = null
    private val wifiReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION == intent.action) {
                val wifiScanResults = wifiManager?.scanResults
                wifiScanResults?.forEach { scanResult: ScanResult ->
                    counterWifi++
                }
            }
        }
    }

    init {
        wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
    }

    fun startScanning() {
        context.registerReceiver(wifiReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        wifiManager?.startScan()
    }

    fun stopScanning() {
        context.unregisterReceiver(wifiReceiver)
    }

    fun getCounterWifi(): Int {
        return counterWifi
    }
    fun setCounter(value: Int) {
        counterWifi = value
    }
}