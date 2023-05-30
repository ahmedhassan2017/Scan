package com.example.scan.wifi
import android.net.wifi.ScanResult
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scan.databinding.ItemScanResultBinding

class ScanResultAdapter(private val scanResults: List<ScanResult>) :
        RecyclerView.Adapter<ScanResultAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemScanResultBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ScanResult) {
            binding.ssidTextView.text = "SSID: ${item.SSID}"
            binding.bssidTextView.text = "BSSID: ${item.BSSID}"
            binding.signalStrengthTextView.text = "Signal Strength: ${item.level}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemScanResultBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scanResults[position])
    }

    override fun getItemCount(): Int {
        return scanResults.size
    }
}
