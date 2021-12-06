package com.kumjinwoo.smartfarm

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*
import com.kumjinwoo.smartfarm.databinding.ActivityLoginBinding
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {

    private var mBinding: ActivityLoginBinding? = null
    private val binding get() = mBinding!!

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun clickLogin(View: View) {
        // var : 가변, val : 불변
        when {
            // 입력이 안 된 곳이 있을 경우 edittext 테투리 변경
            binding.etID.text.toString() == "" -> {
                binding.etID.setBackgroundResource(R.drawable.error_edittext)
                if (binding.etPW.text.toString() == "") {
                    binding.etPW.setBackgroundResource(R.drawable.error_edittext)
                }
            }
            binding.etPW.text.toString() == "" -> {
                binding.etPW.setBackgroundResource(R.drawable.error_edittext)
                binding.etID.setBackgroundResource(R.drawable.round_border_edittext)
            }
            else -> {
                // Edittext 테두리 제거
                binding.etID.setBackgroundResource(R.drawable.round_border_edittext)
                binding.etPW.setBackgroundResource(R.drawable.round_border_edittext)

                //사용자 일치
                database = FirebaseDatabase.getInstance().reference
                database.child("user").addListenerForSingleValueEvent(object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (loginInfo in snapshot.children) {
                            // 아이디 비번 일치 여부 확인
                            if (loginInfo.key.toString() == binding.etID.text.toString() && loginInfo.value.toString() == binding.etPW.text.toString()) {
                                database.child("plant")
                                database.child("plant")
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            var flag = false
                                            for (user in snapshot.children) {
                                                // 식물 1개 이상 있는지 확인
                                                if (user.key.toString() == binding.etID.text.toString()) {
                                                    flag = true
                                                    val intent = Intent(
                                                        this@LoginActivity,
                                                        StateActivity::class.java
                                                    )
                                                    intent.putExtra(
                                                        "USER",
                                                        User(
                                                            binding.etID.text.toString(),
                                                            binding.etPW.text.toString()
                                                        )
                                                    )
                                                    startActivity(intent)
                                                }
                                            }
                                            // 키우는 식물이 없다면, 식물 등록 액티비티로 화면 전환
                                            if (!flag) {
                                                val intent = Intent(
                                                    this@LoginActivity,
                                                    RegistrationActivity::class.java
                                                )
                                                intent.putExtra(
                                                    "USER",
                                                    User(
                                                        binding.etID.text.toString(),
                                                        binding.etPW.text.toString()
                                                    )
                                                )
                                                startActivity(intent)
                                                flag = !flag
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }
                                    })
                            } else {
                                binding.etID.setBackgroundResource(R.drawable.error_edittext)
                                binding.etPW.setBackgroundResource(R.drawable.error_edittext)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
        }
    }
}