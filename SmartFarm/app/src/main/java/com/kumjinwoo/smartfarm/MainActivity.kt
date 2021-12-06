package com.kumjinwoo.smartfarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Testing 현재 시각 가져오기
//        val now: Long = System.currentTimeMillis()
//        val date = Date(now)
//        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale("ko", "KR"))
//        val stringTime = dateFormat.format(date)
//        Log.v("ttt", stringTime)



        //Write a message to the database
//        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
//        val myRef : DatabaseReference = database.getReference("message")
//        myRef.setValue("안녕 반가워!")
    }

    fun clickRegistration(View: View){
        // var : 가변, val : 불변
        var intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    fun clickState(View: View){

        var intent = Intent(this, StateActivity::class.java)
        startActivity(intent)
    }
}