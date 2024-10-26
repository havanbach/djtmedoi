package com.example.djtmedoi.View;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.djtmedoi.Adapter.MessageAdapter;
import com.example.djtmedoi.ChatBot.MsgModal;
import com.example.djtmedoi.ChatBot.RetrofitAPI;
import com.example.djtmedoi.Models.Chat;
import com.example.djtmedoi.Models.User;
import com.example.djtmedoi.Notification.MyApplication;
import com.example.djtmedoi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessageActivity extends AppCompatActivity {

    String userid;
    private CircleImageView profileImageMessage;
    private TextView tvUserMessage;
    private ImageButton btnSendMessage;
    private EditText edtSendMessage;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Intent intent;

    MessageAdapter messageAdapter;
    List<Chat> mChat;
    RecyclerView rcvMessage;

    ValueEventListener seenListener;

    boolean notify = false;
    private int whomess;

    private String read;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        Toolbar toolbar = findViewById(R.id.toolbar_message);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        rcvMessage = findViewById(R.id.rcv_message);
        rcvMessage.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rcvMessage.setLayoutManager(linearLayoutManager);

        profileImageMessage = findViewById(R.id.profile_image_message);
        tvUserMessage = findViewById(R.id.tv_username_message);
        btnSendMessage = findViewById(R.id.btn_send_message);
        edtSendMessage = findViewById(R.id.edt_send_message);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        Log.d("userid", userid);
        whomess = intent.getIntExtra("who", 0);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        if (whomess == 1){
            read = "0Uq5FBdsROECM8irQ2Uq";
        } else if (whomess == 2){
            read = firebaseUser.getUid();
        }

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = edtSendMessage.getText().toString().trim();
                if (!msg.equals("")){
                    if (whomess == 2){
                        getResponse(msg);
                    }
                    sendMessage(read, userid, msg);

                } else {
                    Toast.makeText(MessageActivity.this, "Không thể gửi tin nhắn trống", Toast.LENGTH_SHORT).show();
                }
                edtSendMessage.setText("");
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                tvUserMessage.setText(user.getName());
                if (user.getAvatar().equals("default")){
                    profileImageMessage.setImageResource(R.drawable.background_blue);
                } else {
                    Glide.with(getApplicationContext()).load(user.getAvatar()).into(profileImageMessage);
                }

                readMessage(read, userid, user.getAvatar());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sendPushNotification(String sender, String message) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_food2);
        Intent notifyIntent = new Intent(this, ChatActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, getNotificationId(), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification notification = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setContentTitle(sender)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(bitmap)
                .setAutoCancel(true)    // Tự động xóa Notification sau khi user đã click vào

                // truyền vào notifyPendingIntent đã khai báo ở trên đối với Special Activity
                .setContentIntent(notifyPendingIntent)

                .setColor(getResources().getColor(R.color.Orange))
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
//        notificationManagerCompat.notify(getNotificationId(), notification);
    }

    private int getNotificationId(){
        return (int) new Date().getTime();
    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);
        DatabaseReference chatRef;


        chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(read)
                .child(userid);
        // Add user to chat fragment
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        sendPushNotification(sender, message);



    }


    private void readMessage(String myid, String userid, String imageurl){
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mChat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageurl);
                    Log.d("whomess", whomess+"");
                    messageAdapter.Who(userid);
                    rcvMessage.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        seenMessage(userid);
    }

    private void status(String stastus){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(read);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", stastus);

        reference.updateChildren(hashMap);
    }

//    private void seenMessage(String userid){
//
//        reference = FirebaseDatabase.getInstance().getReference("Chats");
//        seenListener = reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//                    Chat chat = dataSnapshot.getValue(Chat.class);
//                    HashMap<String, Object> hashMap = new HashMap<>();
//                    Log.d("mess", "Người nhận: " + chat.getReceiver() +"\n"+"Người gửi: " + chat.getSender());
//                    if (chat.getReceiver().equals(read) && chat.getSender().equals(userid)) {
//                        hashMap.put("isseen", true);
//                    }
//                    dataSnapshot.getRef().updateChildren(hashMap);
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void getResponse(String message){
        String url = "http://api.brainshop.ai/get?bid=171327&key=ceuNtks1lDgPU0Di&uid=[uid]&msg="+ message;
        String BASE_URL = "http://api.brainshop.ai";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<MsgModal> call = retrofitAPI.getMessage(url);
        call.enqueue(new Callback<MsgModal>() {
            @Override
            public void onResponse(Call<MsgModal> call, Response<MsgModal> response) {
                if (response.isSuccessful()){
                    MsgModal modal = response.body();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", "0Uq5FBdsROECM8irQ2Uq");
                    hashMap.put("receiver", firebaseUser.getUid());
                    hashMap.put("message", modal.getCnt());
                    hashMap.put("isseen", false);
                    reference.child("Chats").push().setValue(hashMap);


                    mChat.add(new Chat("0Uq5FBdsROECM8irQ2Uq", firebaseUser.getUid(), modal.getCnt(), false));
                    messageAdapter.notifyDataSetChanged();
                    rcvMessage.scrollToPosition(mChat.size() - 1);
                }
            }

            @Override
            public void onFailure(Call<MsgModal> call, Throwable t) {
                mChat.add(new Chat("0Uq5FBdsROECM8irQ2Uq", firebaseUser.getUid(), "Cung cấp thêm thông tin", false));
                messageAdapter.notifyDataSetChanged();
                rcvMessage.scrollToPosition(mChat.size() - 1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        reference.removeEventListener(seenListener);
        status("offline");
    }
}