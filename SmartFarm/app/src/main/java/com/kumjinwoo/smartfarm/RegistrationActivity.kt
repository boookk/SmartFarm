package com.kumjinwoo.smartfarm

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.firebase.database.*
import com.kumjinwoo.smartfarm.databinding.ActivityRegistrationBinding
import java.text.SimpleDateFormat
import java.util.*

class RegistrationActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var mBinding: ActivityRegistrationBinding? = null
    private val binding get() = mBinding!!

    private lateinit var database: DatabaseReference

//    private val user = "user1"

    var m_select = "관엽 식물"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent: Intent = getIntent()
        val user = intent.getParcelableExtra<User>("USER")

//      스피너 어댑터 적용
        ArrayAdapter.createFromResource(
            this,
            R.array.selectDivision,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinner.adapter = adapter
        }
        binding.spinner.onItemSelectedListener = this

        // EditText Enter Event
        binding.etType.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keycode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keycode == KeyEvent.KEYCODE_ENTER) {
                    val imm: InputMethodManager =
                        getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                    return true
                }
                return false
            }
        })

        // 배경 눌렀을 때 키보드 내려가기
        binding.activityRegistration.setOnClickListener { hideKeyboard() }

        // 등록 버튼
        binding.btnCommit.setOnClickListener(View.OnClickListener {
            when {
                // 입력이 안 된 곳이 있을 경우
                binding.etName.text.toString() == "" -> {
                    binding.etName.setBackgroundResource(R.drawable.error_edittext)
                    if (binding.etType.text.toString() == "") {
                        binding.etType.setBackgroundResource(R.drawable.error_edittext)
                    }
                }
                binding.etType.text.toString() == "" -> {
                    binding.etType.setBackgroundResource(R.drawable.error_edittext)
                    binding.etName.setBackgroundResource(R.drawable.round_border_edittext)
                }
                else -> {
                    // Edittext 테두리 제거
                    binding.etName.setBackgroundResource(R.drawable.round_border_edittext)
                    binding.etType.setBackgroundResource(R.drawable.round_border_edittext)
                    // 특수문자 제거
                    binding.etName.setText(binding.etName.text.toString().replace("\n", ""))
                    binding.etName.setText(binding.etName.text.toString().replace("\t", ""))
                    binding.etType.setText(binding.etType.text.toString().replace("\n", ""))
                    binding.etType.setText(binding.etType.text.toString().replace("\t", ""))
                    // Database에 저장
                    if (user != null) {
                        writeNewPost(
                            user = user.user,
                            plantName = binding.etName.text.toString(),
                            type = binding.etType.text.toString(),
                            division = m_select
                        )
                    }
                    // Edittext, spinner 값 초기화
                    binding.etName.text = null
                    binding.etType.text = null
                    binding.spinner.setSelection(0)
                    m_select = "관엽 식물"
                    // 알림 메시지
                    Toast.makeText(this, "식물이 등록되었습니다.", Toast.LENGTH_LONG).show()
                }
            }
        })

        // 데이터베이스
        database = FirebaseDatabase.getInstance().reference
    }


    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val curr = parent.getItemAtPosition(pos).toString()
        if (curr != m_select) {
            Toast.makeText(this, curr, Toast.LENGTH_LONG).show()
            m_select = curr
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }


    private fun writeNewPost(user: String, plantName: String, type: String, division: String) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        val key = database.child("plant").push().key
        if (key == null) {
            Log.w(TAG, "Couldn't get push key for posts")
            return
        }
        // 현재 시간 = 재배 시작 시간
        val now: Long = System.currentTimeMillis()
        val date = Date(now)
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale("ko", "KR"))
        val startDate = dateFormat.format(date)

        val post = Post(user, startDate, plantName, type, division)
        val postValues = post.toMap()

        val childUpdates = hashMapOf<String, Any>("/plant/$user/$key" to postValues)

        database.updateChildren(childUpdates)
    }

    fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etName.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.etType.windowToken, 0)
    }
}