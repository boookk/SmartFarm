package com.kumjinwoo.smartfarm

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.*
import com.kumjinwoo.smartfarm.databinding.ActivitySensorInfoBinding

class SensorInfoActivity : AppCompatActivity() {

    private var mBinding: ActivitySensorInfoBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sensor_info)
        mBinding = ActivitySensorInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}