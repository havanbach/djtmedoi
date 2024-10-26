package com.example.djtmedoi.View.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.djtmedoi.R;
import com.example.djtmedoi.View.ChatActivity;
import com.example.djtmedoi.View.SignInActivity;

public class AdminHomeActivity extends AppCompatActivity {

    private CardView cHoaDon, cThongKe, cAddProduct, cSignOut, cAddUser, cChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        Init();
        Event();

    }

    private void Event() {

        cChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, ChatActivity.class);
                intent.putExtra("message", 1);
                startActivity(intent);
            }
        });

        cAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminUsersActivity.class);
                startActivity(intent);
            }
        });
        cHoaDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminBillMainActivity.class);
                startActivity(intent);
            }
        });

        cThongKe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminChartBillActivity.class);
                startActivity(intent);
            }
        });
        cAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminProductActivity.class);
                startActivity(intent);
            }
        });

        cSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void Init() {
        cChat = findViewById(R.id.cChat);
        cAddUser = findViewById(R.id.cAddUser);
        cHoaDon = findViewById(R.id.cHoaDon);
        cThongKe = findViewById(R.id.cThongKe);
        cAddProduct = findViewById(R.id.cAddProduct);
        cSignOut = findViewById(R.id.cSignOut);
    }
}