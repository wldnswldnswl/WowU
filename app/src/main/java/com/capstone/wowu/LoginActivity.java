package com.capstone.wowu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.capstone.wowu.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 로그인 버튼
        Button button1 = (Button)findViewById(R.id.loginButton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, com.capstone.wowu.MainActivity.class);
                startActivity(intenteeeeee);
            }
        });

        // 회원가입 버튼
        Button button2 = (Button)findViewById(R.id.registerButton);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, com.capstone.wowu.RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
