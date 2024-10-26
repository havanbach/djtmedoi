package com.example.djtmedoi.Models;

import com.example.djtmedoi.my_interface.IBinhLuan;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Binhluan {
    private String idbinhluan;
    private String idproduct;
    private String iduser;
    private String rate;
    private String noidung;
    private IBinhLuan callback;
    private FirebaseFirestore db;

    public Binhluan(IBinhLuan callback){
        this.callback = callback;
        db = FirebaseFirestore.getInstance();
    }


    public Binhluan(String idbinhluan, String idproduct, String iduser, String rate, String noidung) {
        this.idbinhluan = idbinhluan;
        this.idproduct = idproduct;
        this.iduser = iduser;
        this.rate = rate;
        this.noidung = noidung;
    }

    public String getIdbinhluan() {
        return idbinhluan;
    }

    public void setIdbinhluan(String idbinhluan) {
        this.idbinhluan = idbinhluan;
    }

    public String getIdproduct() {
        return idproduct;
    }

    public void setIdproduct(String idproduct) {
        this.idproduct = idproduct;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getNoidung() {
        return noidung;
    }

    public void setNoidung(String noidung) {
        this.noidung = noidung;
    }

    public void HandleGetBinhLuanLimit(String idproduct){
        db.collection("BinhLuan").whereEqualTo("idproduct", idproduct).limit(2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot q : queryDocumentSnapshots){
                    callback.getDataBinhLuan(q.getId(), idproduct, q.getString("iduser"),
                            q.getString("rate"), q.getString("noidung"));
                }
            }
        });
    }

    public void HandleGetAllBinhLuan(String idproduct){
        db.collection("BinhLuan").whereEqualTo("idproduct", idproduct).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot q : queryDocumentSnapshots){
                    callback.getDataBinhLuan(q.getId(), idproduct, q.getString("iduser"),
                            q.getString("rate"), q.getString("noidung"));
                }
            }
        });
    }
}
