package com.kumjinwoo.smartfarm

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Point
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*
import com.kumjinwoo.smartfarm.databinding.ActivityStateBinding
import kotlinx.android.synthetic.main.activity_sensor_info.*
import kotlinx.android.synthetic.main.activity_state.*
import java.text.SimpleDateFormat
import java.util.*
import android.view.WindowManager




class StateActivity : AppCompatActivity() {

    private var mBinding: ActivityStateBinding? = null
    private val binding get() = mBinding!!

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityStateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent: Intent = getIntent()
        val user = intent.getParcelableExtra<User>("USER")

        database = FirebaseDatabase.getInstance().reference
        if (user != null) {
            database.child("plant").child(user.user)
                .addValueEventListener(object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (value in snapshot.children) {
                            // 재배 기간 구하기
                            val strDate = value.child("startDate").value.toString()
                            val day = getDay(strDate)

                            // 식물 정보 표시
                            binding.tvName.text = value.child("plantName").value.toString()
                            binding.tvType.text = value.child("type").value.toString()
                            binding.tvDivision.text = value.child("division").value.toString()
                            binding.tvRange.setText(
                                setRange(strDate, "+$day"),
                                TextView.BufferType.SPANNABLE
                            )
                            break
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

            // 자동 제어
            database.child("Auto").child(user.user)
                .addValueEventListener(object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (value in snapshot.children) {
                            when {
                                value.key == "Co2" -> {
                                    binding.swCo2.isChecked = value.value as Boolean
                                }
                                value.key == "Light" -> {
                                    binding.swLight.isChecked = value.value as Boolean
                                }
                                value.key == "Temp" -> {
                                    binding.swTemp.isChecked = value.value as Boolean
                                }
                                value.key == "Water" -> {
                                    binding.swWater.isChecked = value.value as Boolean
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

            // 자동 제어 버튼 상태를 데이터 베이스에 저장
            binding.swCo2.setOnCheckedChangeListener { buttonView, isChecked ->
                database.child("Auto").child(user.user).child("Co2")
                    .setValue(binding.swCo2.isChecked)
            }
            binding.swLight.setOnCheckedChangeListener { buttonView, isChecked ->
                database.child("Auto").child(user.user).child("Light")
                    .setValue(binding.swLight.isChecked)
            }
            binding.swTemp.setOnCheckedChangeListener { buttonView, isChecked ->
                database.child("Auto").child(user.user).child("Temp")
                    .setValue(binding.swTemp.isChecked)
            }
            binding.swWater.setOnCheckedChangeListener { buttonView, isChecked ->
                database.child("Auto").child(user.user).child("Water")
                    .setValue(binding.swWater.isChecked)
            }

            // 수동 제어 버튼 눌렀을 경우
            binding.btnWater.setOnClickListener {
                database.child("Manual").child(user.user).child("Water").setValue(true.toString())
            }
            binding.btnLed.setOnClickListener {
                database.child("Manual").child(user.user).child("Light").setValue(true.toString())
            }
            binding.btnFan.setOnClickListener {
                database.child("Manual").child(user.user).child("Fan").setValue(true.toString())
            }

            // Sensor value 세부정보에서 쓰일 코드 테스트
            binding.btnSensor.setOnClickListener {
                var builder = AlertDialog.Builder(this)
                var view = layoutInflater.inflate(R.layout.activity_sensor_info, null)
                builder.setView(view)

                val tv1 = view.findViewById<TextView>(R.id.tvTemp)
                val tv2 = view.findViewById<TextView>(R.id.tvVWC)
                val tv3 = view.findViewById<TextView>(R.id.tvCo2)
                val tv4 = view.findViewById<TextView>(R.id.tvLight)
                // 센서 값 표시
                database = FirebaseDatabase.getInstance().reference
//                database.child("Sensor").child(user.user).limitToLast(10)
                database.child("Sensor").child(user.user)
                    .addValueEventListener(object : ValueEventListener {
                        @SuppressLint("SetTextI18n")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (value in snapshot.children) {
                                val info = value.value.toString().split(" ")
                                tv1.append(info[0] + "\n")
                                tv2.append(info[1] + "\n")
                                tv3.append(info[2] + "\n")
                                tv4.append(info[3] + "\n")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                builder.setNegativeButton("확인", null)
                builder.show()

                fun resizingViewHeight() {
                    val deviceHeight = getDeviceHeight()
                    resizeViewHeight(deviceHeight, view, 0.05f)
                }

//                resizingViewHeight()
            }
//            database.child("Sensor").child(user.user).limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
////                    TODO("Not yet implemented")
//                    for (value in snapshot.children) {
//                        Log.d("ttt", value.toString())
//                    }
//
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//            })

            // 센서 값 표시
            database.child("Sensor").child(user.user).limitToLast(1)
                .addValueEventListener(object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (value in snapshot.children) {
                            val info = value.value.toString().split(" ")
                            binding.tvTemp.text = info[0]
                            binding.tvWater.text = info[1]
                            binding.tvCo2.text = info[2]
                            binding.tvLight.text = info[3]
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

            // 자동 제어
            database.child("Auto").addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (value in snapshot.children) {
                        break
                    }
                    // 조도
//                val light = snapshot.child("Light").value.toString()
//                binding.tvLight.text = light
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    private fun getDay(day: String): Long {
        // 재배 시작 날짜 구하기
        val dateFormat = SimpleDateFormat("yyyy/MM/dd ", Locale("ko", "KR"))
        val startDate = dateFormat.parse(day).time

        // 재배 기간 구하기
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time.time

        return (today - startDate) / (24 * 60 * 60 * 1000)
    }

    private fun setRange(date: String, day: String): SpannableString {
        val tab = "\t\t\t\t"
        val start = date.length + tab.length
        val end = date.length + tab.length + day.length
        val spannable = SpannableString("$date$tab$day")
        // 재배 기간 글자 색상 변경
        spannable.setSpan(
            ForegroundColorSpan(getColor(R.color.blue)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // 재배 기간 글자 스타일 변경
        spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 재배 기간 글자 크기 변경
        spannable.setSpan(
            RelativeSizeSpan(1.3f),
            start,
            end,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        return spannable
    }

    fun Context.getDeviceHeight(): Int { // 디바이스의 높이를 구한다.

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= 30) {
            windowManager.currentWindowMetrics.bounds.height()
        } else {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            size.y
        }
    }

    fun resizeViewHeight(deviceHeight: Int, view: View, height: Float) { // 뷰의 크기를 조절한다.
        val layoutParams = view.layoutParams
        layoutParams.height = (deviceHeight * height).toInt()
        view.layoutParams = layoutParams
    }



}