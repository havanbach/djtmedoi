package com.example.djtmedoi.Models;

import com.example.djtmedoi.my_interface.IUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;

public class User implements Serializable {
    private String iduser;
    private String email;
    private String name;
    private String address;
    private String phone;
    private String date;
    private String sex;
    private String avatar;
    private FirebaseFirestore db;
    private IUser callback;

    // Chat
    private String status;
    private String search;

    public User() {
    }

    public User(IUser callback) {
        this.callback = callback;
        db = FirebaseFirestore.getInstance();
    }

    public User(String iduser, String name, String avatar, String status, String search) {
        this.iduser = iduser;
        this.name = name;
        this.avatar = avatar;
        this.status = status;
        this.search = search;
    }



    public User(String iduser, String email, String name, String address, String phone, String date, String sex, String avatar) {
        this.iduser = iduser;
        this.email = email;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.date = date;
        this.sex = sex;
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public void HandleGetUsers(String iduser, String email){
        db.collection("User").document(iduser)
                .collection("Profile").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot q : queryDocumentSnapshots){
                    callback.getDataUser(iduser, email, q.getString("hoten")
                            , q.getString("diachi"), q.getString("sdt"), q.getString("ngaysinh"), q.getString("gioitinh")
                            , q.getString("avatar"));
                }
            }
        });
    }
}
