package com.example.djtmedoi.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.djtmedoi.Adapter.UserAdapter;
import com.example.djtmedoi.Models.ChatList;
import com.example.djtmedoi.Models.User;
import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.IClickCTHD;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ProgressBar progressBarChat;
    private RecyclerView recyclerViewChat;
    private List<User> mUsers;
    private UserAdapter userAdapter;
    private ImageView imgBackChat;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private List<ChatList> usersList;

    private int who;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        InitWidget();
        Init();

        // nếu là admin đăng nhập
        if (who == 1){
            reference = FirebaseDatabase.getInstance().getReference("Chatlist").child("0Uq5FBdsROECM8irQ2Uq");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usersList.clear();
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        ChatList chatlist = dataSnapshot.getValue(ChatList.class);
                        usersList.add(chatlist);
                    }
                    UsersChatWithAdmin();
                    progressBarChat.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else { // nếu là user đăng nhập
            reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usersList.clear();
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        ChatList chatlist = dataSnapshot.getValue(ChatList.class);
                        usersList.add(chatlist);
                    }
                    AdminChatsWithUsers();
                    progressBarChat.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        imgBackChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }

    private void Init() {
        recyclerViewChat.setHasFixedSize(true);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();
        mUsers = new ArrayList<>();
        progressBarChat.setVisibility(View.VISIBLE);
        who = getIntent().getIntExtra("message", 0);
    }

    private void InitWidget() {
        imgBackChat = findViewById(R.id.img_back_chat);
        progressBarChat = findViewById(R.id.progressBar_chat);
        recyclerViewChat = findViewById(R.id.recycler_view_chat);
    }

    private void AdminChatsWithUsers() {
        Log.d("account", "user đăng nhập");
        mUsers.clear();
        mUsers.add(new User("0Uq5FBdsROECM8irQ2Uq", "Admin", "default", "offline", "Admin"));
        userAdapter = new UserAdapter(ChatActivity.this, mUsers, true, new IClickCTHD() {
            @Override
            public void onClickCTHD(int pos) {
                User user = mUsers.get(pos);
                Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
                intent.putExtra("userid", user.getIduser());
                intent.putExtra("who", 2);
                startActivity(intent);
            }
        });
        recyclerViewChat.setAdapter(userAdapter);
    }

    private void UsersChatWithAdmin(){
        Log.d("account", "admin đăng nhập");
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    for (ChatList chatlist: usersList){
                        if (user.getIduser().equals(chatlist.getId())){
                            mUsers.add(user);
                        }

                    }

                }
                userAdapter = new UserAdapter(ChatActivity.this, mUsers, true, new IClickCTHD() {
                    @Override
                    public void onClickCTHD(int pos) {
                        User user = mUsers.get(pos);
                        Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
                        intent.putExtra("userid", user.getIduser());
                        intent.putExtra("who", 1);
                        startActivity(intent);
                    }
                });
                recyclerViewChat.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String stastus){
        if (who == 1){
            reference = FirebaseDatabase.getInstance().getReference("Users").child("0Uq5FBdsROECM8irQ2Uq");
        } else {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", stastus);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}