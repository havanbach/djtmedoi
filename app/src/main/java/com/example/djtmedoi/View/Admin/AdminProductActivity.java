package com.example.djtmedoi.View.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.djtmedoi.Models.Product;
import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.IClickCTHD;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminProductActivity extends AppCompatActivity {

    private RecyclerView rcvAdminProduct;
    private AppCompatImageView imgAddProduct;
    private ArrayList<Product> mlistProduct;
    private AdminProductAdapter adapter;
    private ImageView imgBackAdminProduct;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Spinner spinnerLoaiSP;
    private ArrayList<String> mlistLoaiSP;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_product);

        InitWidget();
        Init();
        HandleGetDataAllProduct();
        Event();
    }

    private void Event() {
        imgAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminProductActivity.this, AdminAddSPActivity.class);
                startActivity(intent);
            }
        });

        imgBackAdminProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void HandleGetDataAllProduct() {
        db.collection("SanPham").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0) {
                    for (QueryDocumentSnapshot d : queryDocumentSnapshots) {
                        // Kiểm tra và lấy giá trị giatien, soluong
                        Long giatien = d.getLong("giatien");
                        Long soluong = d.getLong("soluong");
                        Long type = d.getLong("type");
                        type = (type != null) ? type : 0L; // Hoặc một giá trị mặc định nào đó

                        // Nếu giatien hoặc soluong là null, sử dụng giá trị mặc định là 0L
                        giatien = (giatien != null) ? giatien : 0L; // Giá trị mặc định cho giatien
                        soluong = (soluong != null) ? soluong : 0L; // Giá trị mặc định cho soluong

                        // Thêm sản phẩm vào danh sách
                        mlistProduct.add(new Product(
                                d.getId(),
                                d.getString("tensp"),
                                giatien,
                                d.getString("hinhanh"),
                                d.getString("loaisp"),
                                d.getString("mota"),
                                soluong,
                                d.getString("size"),
                                d.getLong("type"), // Kiểm tra null nếu cần
                                d.getString("chatlieu")
                        ));
                    }
                    adapter = new AdminProductAdapter(AdminProductActivity.this, mlistProduct, new IClickCTHD() {
                        @Override
                        public void onClickCTHD(int pos) {
                            Product product = mlistProduct.get(pos);
                            Intent intent = new Intent(AdminProductActivity.this, AdminAddSPActivity.class);
                            intent.putExtra("SP", product);
                            startActivity(intent);
                        }
                    });
                    rcvAdminProduct.setLayoutManager(new LinearLayoutManager(AdminProductActivity.this, RecyclerView.VERTICAL, false));
                    rcvAdminProduct.setAdapter(adapter);
                }
            }
        });
    }



    private void Init() {
        mlistProduct = new ArrayList<>();

        mlistLoaiSP = new ArrayList<>();

        mlistLoaiSP.add("Tất cả");
        db.collection("LoaiProduct").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot d : queryDocumentSnapshots){
                    mlistLoaiSP.add(d.getString("tenloai"));
                    ArrayAdapter arrayAdapter = new ArrayAdapter(AdminProductActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, mlistLoaiSP);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    spinnerLoaiSP.setAdapter(arrayAdapter);
                    spinnerLoaiSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            String s = mlistLoaiSP.get(i);
                            category = s;
                            if (category.equals("Tất cả")){
                                mlistProduct.clear();
                                if (adapter!=null){
                                    adapter.notifyDataSetChanged();
                                }
                                HandleGetDataAllProduct();
                            } else {
                                mlistProduct.clear();
                                if (adapter!=null){
                                    adapter.notifyDataSetChanged();
                                }
                                db.collection("SanPham").whereEqualTo("loaisp",category)
                                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                                        if(queryDocumentSnapshots.size()>0){
                                            for(QueryDocumentSnapshot d : queryDocumentSnapshots){
                                                mlistProduct.add(new Product(d.getId(),d.getString("tensp"),
                                                        d.getLong("giatien"),d.getString("hinhanh"),
                                                        d.getString("loaisp"),d.getString("mota"),
                                                        d.getLong("soluong"),d.getString("size"),
                                                        d.getLong("type"),d.getString("chatlieu")));
                                            }
                                            adapter = new AdminProductAdapter(AdminProductActivity.this, mlistProduct, new IClickCTHD() {
                                                @Override
                                                public void onClickCTHD(int pos) {
                                                    Product product = mlistProduct.get(pos);
                                                    Intent intent = new Intent(AdminProductActivity.this, AdminAddSPActivity.class);
                                                    intent.putExtra("SP",product);
                                                    startActivity(intent);
                                                }
                                            });
                                            rcvAdminProduct.setLayoutManager(new LinearLayoutManager(AdminProductActivity.this,RecyclerView.VERTICAL,false));
                                            rcvAdminProduct.setAdapter(adapter);
                                        }

                                    }
                                });
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }
        });
    }

    private void InitWidget() {
        rcvAdminProduct = findViewById(R.id.rcv_admin_product);
        imgAddProduct = findViewById(R.id.image_add_product);
        imgBackAdminProduct = findViewById(R.id.img_back_admin_product);
        spinnerLoaiSP = findViewById(R.id.spinner_loai_sp);

    }
}