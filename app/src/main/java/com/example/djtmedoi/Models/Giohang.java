package com.example.djtmedoi.Models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.djtmedoi.my_interface.IGioHang;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class Giohang {
    private String id;
    private String id_product;
    private long soluong;

    private IGioHang callback;
    private FirebaseFirestore db;

    public Giohang() {
    }

    public Giohang(String id, String id_product, long soluong) {
        this.id = id;
        this.id_product = id_product;
        this.soluong = soluong;
    }

    public Giohang(String id_product, long soluong) {
        this.id_product = id_product;
        this.soluong = soluong;
    }

    public Giohang(IGioHang callback){
        this.callback = callback;
        db = FirebaseFirestore.getInstance();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_product() {
        return id_product;
    }

    public void setId_product(String id_product) {
        this.id_product = id_product;
    }

    public long getSoluong() {
        return soluong;
    }

    public void setSoluong(long soluong) {
        this.soluong = soluong;
    }

    public void HandleThanhToan(String ghichu, String ngaydat, String diachi, String hoten, String sdt, String phuongthuc, String tongtien, ArrayList<Product> arrayList) {

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("ghichu", ghichu);
        hashMap.put("ngaydat",ngaydat);
        hashMap.put("diachi",diachi);
        hashMap.put("sdt",sdt);
        hashMap.put("hoten",hoten);
        hashMap.put("phuongthuc",phuongthuc);
        hashMap.put("tongtien",tongtien);
        hashMap.put("trangthai",1);
        hashMap.put("UID",FirebaseAuth.getInstance().getCurrentUser().getUid());
        db.collection("HoaDon")
                .add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    for(Product sanPhamModels : arrayList){
                        HashMap<String,Object> map_chitiet = new HashMap<>();
                        map_chitiet.put("id_hoadon",task.getResult().getId());
                        map_chitiet.put("id_product",sanPhamModels.getIdsp());
                        map_chitiet.put("soluong",sanPhamModels.getSoluong());
                        db.collection("ChitietHoaDon").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("ALL").add(map_chitiet).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if(task.isSuccessful()){
                                    db.collection("GioHang").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .collection("ALL").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot q : queryDocumentSnapshots){
                                                db.collection("GioHang").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .collection("ALL").document(q.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            callback.OnSucess();
                                                        } else {
                                                            callback.OnFail();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });

                                }

                            }
                        });

                    }

                }else{

                }

            }
        });
    }

    //check giỏ hàng đúng id user
    public  void AddCart(String idsp, Long soluong){
        db.collection("GioHang").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("ALL").whereEqualTo("id_product",idsp).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size()!=0){
                    if(queryDocumentSnapshots.size()>0){
                        for(QueryDocumentSnapshot d : queryDocumentSnapshots){
                            long   soluong_sp = d.getLong("soluong");
                            //check so luong sp tăng lên 1
                            if(soluong_sp>0){
                                soluong_sp = soluong_sp + soluong;
                                db.collection("GioHang").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("ALL").document(d.getId()).update("soluong",soluong_sp).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                        }
                    }
                }else{
                    Giohang hangModels = new Giohang(idsp,soluong);
                    db.collection("GioHang").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .collection("ALL").add(hangModels).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                callback.OnSucess();
                            }else {
                                callback.OnFail();
                            }
                        }
                    });
                }
            }
        });
    }

    public  void HandlegetDataGioHang(){
        db.collection("GioHang").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("ALL").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size()>0){
                    for(QueryDocumentSnapshot s : queryDocumentSnapshots){
                        db.collection("SanPham").document(s.getString("id_product"))
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(@NonNull DocumentSnapshot d) {

                                callback.getDataSanPham(s.getId(),s.getString("id_product"),d.getString("tensp"),
                                        d.getLong("giatien"),d.getString("hinhanh"),
                                        d.getString("loaisp"),
                                        d.getString("mota"),
                                        s.getLong("soluong"),d.getString("size"),
                                        d.getLong("type"),d.getString("chatlieu"));


                            }
                        });
                    }
                }

            }
        });
    }
    public  void HandleDeleteDataGioHang(String id){
        db.collection("GioHang").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("ALL").document(id).delete();
    }

    public void HandleGetDataCTHD(String id) {

        db.collection("ChitietHoaDon").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("ALL").whereEqualTo("id_hoadon",id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size()>0){
                    for(QueryDocumentSnapshot s : queryDocumentSnapshots){
                        Log.d("CHECKED",s.getString("id_product"));
                        db.collection("SanPham").document(s.getString("id_product"))
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(@NonNull DocumentSnapshot d) {
                                callback.getDataSanPham(s.getId(),s.getString("id_product"),d.getString("tensp"),
                                        d.getLong("giatien"),d.getString("hinhanh"),
                                        d.getString("loaisp"),
                                        d.getString("mota"),
                                        s.getLong("soluong"),d.getString("size"),
                                        1l,d.getString("chatlieu"));
                            }
                        });
                    }
                }

            }
        });
    }
    public void HandleGetDataCTHD(String id,String uid) {

        if(uid!=null){
            db.collection("ChitietHoaDon").document(uid)
                    .collection("ALL").whereEqualTo("id_hoadon",id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.size()>0){
                        for(QueryDocumentSnapshot s : queryDocumentSnapshots){
//                            Log.d("CHECKED",s.getString("id_product"));
                            db.collection("SanPham").document(s.getString("id_product"))
                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(@NonNull DocumentSnapshot d) {
                                    callback.getDataSanPham(s.getId(),s.getString("id_product"),d.getString("tensp"),
                                            d.getLong("giatien"),d.getString("hinhanh"),
                                            d.getString("loaisp"),
                                            d.getString("mota"),
                                            s.getLong("soluong"),d.getString("size"),
                                            1l,d.getString("chatlieu"));
                                }
                            });
                        }
                    }

                }
            });

        }else{

        }


    }


}
