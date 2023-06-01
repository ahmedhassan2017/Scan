package com.example.scan.bluetooth
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.scan.R
import com.example.scan.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main2)





        binding.textview2.setOnClickListener {

        }

    }

    override fun onResume()
    {
        super.onResume()

    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }


}

