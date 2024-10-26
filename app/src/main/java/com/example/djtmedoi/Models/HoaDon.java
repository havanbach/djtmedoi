package com.example.djtmedoi.Models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.djtmedoi.my_interface.IHoaDon;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;

public class HoaDon implements Serializable {

    private String id;
    private String uid;
    private String ghichu;
    private String diachi;
    private String hoten;
    private String ngaydat;
    private String phuongthuc;
    private String sdt;
    private String tongtien;
    private long type;
    private IHoaDon callback;
    private FirebaseFirestore db;



    public HoaDon(IHoaDon callback) {
        this.callback=callback;
        db=FirebaseFirestore.getInstance();
    }

    public HoaDon(String id, String uid, String ghichu, String diachi, String hoten, String ngaydat, String phuongthuc, String sdt, String tongtien, long type) {
        this.id = id;
        this.uid = uid;
        this.diachi = diachi;
        this.hoten = hoten;
        this.ngaydat = ngaydat;
        this.phuongthuc = phuongthuc;
        this.sdt = sdt;
        this.tongtien = tongtien;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGhichu() {
        return ghichu;
    }

    public void setGhichu(String ghichu) {
        this.ghichu = ghichu;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getNgaydat() {
        return ngaydat;
    }

    public void setNgaydat(String ngaydat) {
        this.ngaydat = ngaydat;
    }

    public String getPhuongthuc() {
        return phuongthuc;
    }

    public void setPhuongthuc(String phuongthuc) {
        this.phuongthuc = phuongthuc;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getTongtien() {
        return tongtien;
    }

    public void setTongtien(String tongtien) {
        this.tongtien = tongtien;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    // TK user: Lấy tất cả hóa đơn theo iduser
    public  void HandleReadData(){
        db.collection("HoaDon").whereEqualTo("UID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size()>0){
                    for(QueryDocumentSnapshot d : queryDocumentSnapshots){
                        callback.getDataHD(d.getId(),d.getString("UID"),d.getString("ghichu"), d.getString("diachi"),
                                d.getString("hoten"),d.getString("ngaydat"),d.getString("phuongthuc"),d.getString("sdt"),
                                d.getString("tongtien"),d.getLong("trangthai"));
                    }
                }

            }
        });
    }

    // TK user: lấy ra hóa đơn của tk user hiện tại, với điều kiện trạng thái nào
    public  void HandleReadDataStatus(int status){
        db.collection("HoaDon").whereEqualTo("UID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("trangthai", status).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0){
                    for(QueryDocumentSnapshot c : queryDocumentSnapshots){
                        Log.d("currenthd", c.getString("UID"));
                        callback.getDataHD(c.getId(),c.getString("UID"),c.getString("ghichu"), c.getString("diachi"),
                                c.getString("hoten"),c.getString("ngaydat"),c.getString("phuongthuc"),c.getString("sdt"),
                                c.getString("tongtien"),c.getLong("trangthai"));
                    }
                }
            }
        });
    }

    // TK admin: update trạng thái bill với id hóa đơn nào, và i tương ứng với trạng thái nào
    public void HandleUpdateStatusBill(int i,String id) {
        db.collection("HoaDon")
                .document(id).update("trangthai",i).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    callback.OnSucess();
                }else{
                    callback.OnFail();
                }
            }
        });
    }



    // TK admin: - Nếu position = 0 thì lấy tất cả hóa đơn của tất cả user
    //           - Ngược lại: lấy ra hóa đơn tương ứng với trạng thái
    public void HandleReadData(String iduser, int positionStatus) {

        if(positionStatus==0 && iduser == null) {
            db.collection("HoaDon")
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.size() > 0) {
                        for (QueryDocumentSnapshot d : queryDocumentSnapshots) {
                            callback.getDataHD(d.getId(), d.getString("UID"), d.getString("ghichu"), d.getString("diachi"),
                                    d.getString("hoten"), d.getString("ngaydat"), d.getString("phuongthuc"), d.getString("sdt"),
                                    d.getString("tongtien"), d.getLong("trangthai"));
                        }
                    }

                }
            });
        } else if (positionStatus == 0){
            db.collection("HoaDon").whereEqualTo("UID",iduser)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.size()>0){
                        for(QueryDocumentSnapshot d : queryDocumentSnapshots){
                            callback.getDataHD(d.getId(),d.getString("UID"),d.getString("ghichu"),d.getString("diachi"),
                                    d.getString("hoten"),d.getString("ngaydat"),d.getString("phuongthuc"),d.getString("sdt"),
                                    d.getString("tongtien"),d.getLong("trangthai"));
                        }
                    }

                }
            });
        } else if (iduser == null){
            db.collection("HoaDon").whereEqualTo("trangthai",positionStatus)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.size()>0){
                        for(QueryDocumentSnapshot d : queryDocumentSnapshots){
                            callback.getDataHD(d.getId(),d.getString("UID"),d.getString("ghichu"),d.getString("diachi"),
                                    d.getString("hoten"),d.getString("ngaydat"),d.getString("phuongthuc"),d.getString("sdt"),
                                    d.getString("tongtien"),d.getLong("trangthai"));
                        }
                    }

                }
            });
        }
        else {
            db.collection("HoaDon").whereEqualTo("UID",iduser).whereEqualTo("trangthai",positionStatus)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.size()>0){
                        for(QueryDocumentSnapshot d : queryDocumentSnapshots){
                            callback.getDataHD(d.getId(),d.getString("UID"),d.getString("ghichu"),d.getString("diachi"),
                                    d.getString("hoten"),d.getString("ngaydat"),d.getString("phuongthuc"),d.getString("sdt"),
                                    d.getString("tongtien"),d.getLong("trangthai"));
                        }
                    }

                }
            });
        }
    }

    public void HandleGetHoaDonDaGiao(){
        db.collection("HoaDon").whereEqualTo("trangthai",3)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size()>0){
                    for(QueryDocumentSnapshot d : queryDocumentSnapshots){
                        callback.getDataHD(d.getId(),d.getString("UID"),d.getString("ghichu"),d.getString("diachi"),
                                d.getString("hoten"),d.getString("ngaydat"),d.getString("phuongthuc"),d.getString("sdt"),
                                d.getString("tongtien"),d.getLong("trangthai"));
                    }
                }

            }
        });
    }
}
