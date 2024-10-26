package com.example.djtmedoi.Models;

import androidx.annotation.NonNull;

import com.example.djtmedoi.my_interface.IFavorite;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Favorite {

    private String idlove;
    private String idproduct;
    private String iduser;
    private IFavorite callback;
    private FirebaseFirestore db;


    public Favorite(IFavorite callback) {
        this.callback = callback;
        db=FirebaseFirestore.getInstance();
    }

    public Favorite() {
    }

    public Favorite(String idlove, String idproduct, String iduser) {
        this.idlove = idlove;
        this.idproduct = idproduct;
        this.iduser = iduser;
    }

    public String getIdlove() {
        return idlove;
    }

    public void setIdlove(String idlove) {
        this.idlove = idlove;
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

    public  void HandleGetFavorite(String iduser){
        db.collection("Favorite").whereEqualTo("iduser", iduser)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size()>0){
                    for(QueryDocumentSnapshot d : queryDocumentSnapshots){
                        callback.getDataFavorite(d.getId(), d.getString("idproduct"), iduser);
                    }
                }

            }
        });
    }


}
