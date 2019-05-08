package com.capstone.wowu

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity:AppCompatActivity(){
    var auth:FirebaseAuth?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth=FirebaseAuth.getInstance()
    }
}