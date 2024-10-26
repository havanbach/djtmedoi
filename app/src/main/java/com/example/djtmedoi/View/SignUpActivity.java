package com.example.djtmedoi.View;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.djtmedoi.MainActivity;
import com.example.djtmedoi.Models.User;
import com.example.djtmedoi.R;
import com.example.djtmedoi.ultil.MyReceiver;
import com.example.djtmedoi.ultil.NetworkUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtSignUpEmail, edtSignUpPassword, edtSignUpConfirm;
    private Button btnSignUpDangKy;
    private TextView tvLoginUser;

    DatabaseReference reference1, reference2;

    // Check Internet
    private BroadcastReceiver MyReceiver = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        InitWidget();

        MyReceiver = new MyReceiver();      // Check Internet
        broadcastIntent();                  // Check Internet
        if (NetworkUtil.isNetworkConnected(this)){
            Event();
        }
    }

    private void Event() {
        btnSignUpDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtSignUpEmail.getText().toString().trim();
                String pass = edtSignUpPassword.getText().toString().trim();
                String confirm = edtSignUpConfirm.getText().toString().trim();
                if (email.length() > 0){
                    if (pass.length() > 0){
                        if (pass.equals(confirm)){
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        HashMap<String,String> hashMap =  new HashMap<>();
                                        hashMap.put("iduser",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        hashMap.put("email", email);
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("IDUser").add(hashMap);

                                        // Realtime Firebase: Tạo 1 database có tên Users, id tự động đặt cho tài khoản
                                        String username= "any name";
                                        reference1 = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        HashMap<String, String> mapRealtime = new HashMap<>();
                                        mapRealtime.put("iduser", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        mapRealtime.put("name", username);
                                        mapRealtime.put("avatar", "default");
                                        mapRealtime.put("status", "online");
                                        mapRealtime.put("search", username.toLowerCase());
                                        reference1.setValue(mapRealtime);

                                        reference2 = FirebaseDatabase.getInstance().getReference("Chatlist").child("WvPK8OV0erKJP8w2KZNp")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        HashMap<String, String> mapRealtime2 = new HashMap<>();
                                        mapRealtime2.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        reference2.setValue(mapRealtime2);


                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        User user = new User();
                                        user.setIduser(auth.getUid());
                                        user.setEmail(email);
                                        finishAffinity();
                                        Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                    } else if (!isEmailValid(email)){
                                        Toast.makeText(SignUpActivity.this, "Email định dạng không đúng", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        Log.w("signup","failed", task.getException());
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(SignUpActivity.this, "Mật khẩu xác nhận không khớp.\nVui lòng nhập lại!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Bạn chưa nhập mật khẩu", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(SignUpActivity.this, "Bạn chưa nhập Email", Toast.LENGTH_SHORT).show();
                }

            }
        });

        tvLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void InitWidget() {
        edtSignUpEmail = findViewById(R.id.edt_sign_up_email);
        edtSignUpPassword = findViewById(R.id.edt_sign_up_password);
        edtSignUpConfirm = findViewById(R.id.edt_sign_up_confirm);
        btnSignUpDangKy = findViewById(R.id.btn_sign_up_dangky);
        tvLoginUser = findViewById(R.id.tv_log_in_user);
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Check Internet
    public void broadcastIntent() {
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    // Check Internet
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(MyReceiver);
    }
}