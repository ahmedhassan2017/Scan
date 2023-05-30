package com.example.scan.wifi_direct
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import android.util.Log

class WifiDirectManager(private val context: Context) {

    companion object{
        val TAG ="ahmed"
    }

    private val wifiP2pManager: WifiP2pManager by lazy {
        context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    }
    private val channel: WifiP2pManager.Channel by lazy {
        wifiP2pManager.initialize(context, Looper.getMainLooper(), null)
    }

    private val peerListListener = WifiP2pManager.PeerListListener { peerList: WifiP2pDeviceList? ->
        peerList?.deviceList?.let { devices ->
            // Handle the list of nearby devices
            for (device in devices) {
                val deviceName = device.deviceName
                val deviceAddress = device.deviceAddress
                // Do whatever you want with the device information
                Log.i(TAG, "Device Name: $deviceName, Device Address: $deviceAddress")
            }
        }
    }

    fun discoverPeers() {
        wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                // Discovery initiation successful. Perform any additional operations if needed.
                Log.i(TAG, "onSuccess: Called")
            }

            override fun onFailure(reason: Int) {
                // Discovery initiation failed. Handle the error.
                when (reason) {
                    WifiP2pManager.ERROR -> {
                        // An error occurred. Display an error message or take appropriate action.
                        Log.i(TAG, "onFailure : Error")
                    }
                    WifiP2pManager.P2P_UNSUPPORTED -> {
                        // Wi-Fi Direct is not supported on this device. Display a message or take appropriate action.
                        Log.i(TAG, "onFailure: Wi-Fi Direct is not supported on this device")
                    }
                    WifiP2pManager.BUSY -> {
                        // Wi-Fi Direct is busy. Retry the discovery process later or display a message.
                        Log.i(TAG, "onFailure:  Wi-Fi Direct is busy" )
                    }
                }
            }
        })
    }

    fun stopPeerDiscovery() {
        wifiP2pManager.stopPeerDiscovery(channel, null)
    }

    fun registerReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        }
        context.registerReceiver(WifiDirectReceiver(), intentFilter)
    }

    fun unregisterReceiver() {
        context.unregisterReceiver(WifiDirectReceiver())
    }

    inner class WifiDirectReceiver : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                when (it.action) {
                    WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                        wifiP2pManager.requestPeers(channel, peerListListener)
                    }
                }
            }
        }
    }
}
