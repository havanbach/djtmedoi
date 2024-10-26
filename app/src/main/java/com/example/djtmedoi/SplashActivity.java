package com.example.djtmedoi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.djtmedoi.View.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable() {
            @Override
            // check ktra đã đăng nhập vào thẳng vào Home
            public void run() {
                nextActivity();
            }

        }, 2000);
    }

    private void nextActivity() {
        // Check user đã login hay chưa, Firebase sẽ cung cấp 1 hàm lấy dc thông tin của user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Thực hiện check đối tượng user để biết người dùng đã login hay chưa
        if (user == null) {
            // Chưa login
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        } else {
            // Đã login
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }
}