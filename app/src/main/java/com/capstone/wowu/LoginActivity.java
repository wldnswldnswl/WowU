package com.capstone.wowu;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.capstone.wowu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LogInActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private EditText userEmail;
    private EditText userPwd;

    private Button LogInButton;
    private Button RegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        userEmail = findViewById(R.id.editEmail);
        userPwd = findViewById(R.id.editPwd);

        LogInButton = findViewById(R.id.loginButton);
        RegisterButton = findViewById(R.id.registerButton);

        LogInButton.setOnClickListener(this);
        RegisterButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void logIn() {
        Log.d(TAG, "logIn");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = userEmail.getText().toString();

        String password = userPwd.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "LogIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(LoginActivity.this, "Log In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void onAuthSuccess (FirebaseUser user){
        // Write new user
        writeNewUser(user.getUid(), user.getEmail());

        // Go to MainActivity
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    // [START basic_write]
    private void writeNewUser (String userId, String email){
        User user = new User("test",email);

        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(userEmail.getText().toString())) {
            userEmail.setError("Required");
            result = false;
        } else {
            userEmail.setError(null);
        }

        if (TextUtils.isEmpty(userPwd.getText().toString())) {
            userPwd.setError("Required");
            result = false;
        } else {
            userPwd.setError(null);
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.loginButton) {
            logIn();
        } else if (i == R.id.registerButton) {
            Intent intent = new Intent(LoginActivity.this, com.capstone.wowu.RegisterActivity.class);
            startActivity(intent);
        }
    }

}

