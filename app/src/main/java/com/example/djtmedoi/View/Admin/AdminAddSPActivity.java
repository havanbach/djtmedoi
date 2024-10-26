package com.example.djtmedoi.View.Admin;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.example.djtmedoi.Models.Product;
import com.example.djtmedoi.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminAddSPActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView tvTaoMaQR;
    private ImageView imgQRProduct;
    private Button btnQRProduct, btnDownQRProduct;

    private CircleImageView imgAddLoaiProduct;
    private ImageView btnAddBack, btnRefresh, btnSave;
    private EditText edtTenSP, edtGiatienSP, edtsizeSP, edtchatlieuSP, edtSoluongSP, edtTypeSP, edtMotaSP;
    private ImageView imgAdd;
    private Button btnDanhmuc, btnDelete;
    private AppCompatButton btnEdit;
    private Spinner spinnerDanhMuc;
    private TextView tvTitle;

    private FirebaseFirestore db;
    private List<String> list;
    private Product product;
    private String image = "";
    private static final int LIBRARY_PICKER = 12312;
    private ProgressDialog dialog;
    private String loaisp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_spactivity);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        InitWidget();
        Init();

//        // Test: Lấy ra id của hóa đơn từ bảng HoaDon
//        db.collection("HoaDon").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                for (QueryDocumentSnapshot q: queryDocumentSnapshots){
//                    ArrayList<String> list = new ArrayList<>();
//                    list.add(q.getId());
//                    Log.d("checkid", q.getId());
//                }
//            }
//        });
        Event();
    }

    private void Event() {
        btnAddBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgAddLoaiProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminAddSPActivity.this, AdminAddLoaiSPActivity.class);
                intent.putExtra("loaisp", loaisp);
                startActivity(intent);
            }
        });

        btnDanhmuc.setOnClickListener(view -> spinnerDanhMuc.performClick());
        spinnerDanhMuc.setOnItemSelectedListener(this);
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtsizeSP.setText("");
                edtchatlieuSP.setText("");
                edtTenSP.setText("");
                edtGiatienSP.setText("");
                edtTypeSP.setText("");
                edtMotaSP.setText("");
                edtSoluongSP.setText("");
                image = "";
                imgAdd.setImageResource(R.drawable.pl);
            }
        });

        // Nếu xóa bất kỳ 1 sản phẩm nào đó thì những hóa đơn có chứa sản phẩm đó cũng phải bị xóa hoặc dùng nhiều cách khác.
        // Ở đây lựa chọn xóa luôn hóa đơn chứa sản phẩm bị xóa.
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("SanPham").document(product.getId()).delete().addOnSuccessListener(unused -> {
                    db.collection("IDUser").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot q: queryDocumentSnapshots){
                                Log.d("checkiduser", q.getString("iduser"));

                                // Từ iduser mà ta có, lấy ra tất cả id_hoadon có id_product là KlUnpxIGoIFkHlvshil2
                                db.collection("ChitietHoaDon").document(q.getString("iduser")).
                                        collection("ALL").whereEqualTo("id_product", product.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot d: queryDocumentSnapshots){
                                            Log.d("checkidhoadon", d.getString("id_hoadon"));

                                            // Từ id_hoadon mà ta có, thực hiện xóa id hóa đơn của bảng HoaDon
                                            db.collection("HoaDon").document(d.getString("id_hoadon")).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(AdminAddSPActivity.this, "Xoá sản phẩm thành công!!!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
//                                            db.collection("QRproduct").document(product.getId()).delete();
                                        }
                                    }
                                });
                            }

                        }
                    });

                    setResult(RESULT_OK);
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(AdminAddSPActivity.this, "Xoá sản phẩm thất bại!!!", Toast.LENGTH_SHORT).show();
                });

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validate()) {
                    return;
                }
                try {
                    Product sp = new Product();
                    sp.setGiatien(Long.parseLong(edtGiatienSP.getText().toString()));
                    sp.setMota(edtMotaSP.getText().toString());
                    sp.setsize(edtsizeSP.getText().toString());
                    sp.setType(Long.parseLong(edtTypeSP.getText().toString()));
                    sp.setTensp(edtTenSP.getText().toString());
                    sp.setSoluong(Long.parseLong(edtSoluongSP.getText().toString()));
                    sp.setchatlieu(edtchatlieuSP.getText().toString());
                    sp.setLoaisp(spinnerDanhMuc.getSelectedItem().toString());
                    sp.setHinhanh(image);

                    db.collection("SanPham").add(sp).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(@NonNull DocumentReference documentReference) {
                            Toast.makeText(AdminAddSPActivity.this, "Thành công!!!", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdminAddSPActivity.this, "Thất bại!!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validate()) {
                    return;
                }
                try {
                    Product sp = new Product();
                    sp.setGiatien(Long.parseLong(edtGiatienSP.getText().toString()));
                    sp.setMota(edtMotaSP.getText().toString());
                    sp.setsize(edtsizeSP.getText().toString());
                    sp.setType(Long.parseLong(edtTypeSP.getText().toString()));
                    sp.setTensp(edtTenSP.getText().toString());
                    sp.setSoluong(Long.parseLong(edtSoluongSP.getText().toString()));
                    sp.setchatlieu(edtchatlieuSP.getText().toString());
                    sp.setLoaisp(spinnerDanhMuc.getSelectedItem().toString());
                    sp.setHinhanh(image);
                    db.collection("SanPham").document(product.getId()).set(sp)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void unused) {
                                    Toast.makeText(AdminAddSPActivity.this, "Cập nhật sản phẩm thành công!!!", Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdminAddSPActivity.this, "Cập nhật sản phẩm thất bại!!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnQRProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiFormatWriter writer = new MultiFormatWriter();
                try {

                    BitMatrix matrix = writer.encode(product.getId(), BarcodeFormat.QR_CODE, 350, 350);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap bitmap = encoder.createBitmap(matrix);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    String filename = System.currentTimeMillis() + "";
                    StorageReference storageReference;
                    storageReference = FirebaseStorage.getInstance("gs://doan-dc57a.appspot.com/").getReference();
                    storageReference.child("QRProduct").child(filename + ".jpg").putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getTask().isSuccessful()) {
                                storageReference.child("QRProduct").child(filename + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(@NonNull Uri uri) {
                                        imgQRProduct.setImageBitmap(bitmap);
                                        HashMap<String, String> maps = new HashMap<>();
                                        maps.put("idproduct", product.getId());
                                        maps.put("hinhanh_qr", uri.toString());
                                        db.collection("QRProduct").add(maps);
                                    }
                                });

                            }
                            dialog.dismiss();
                        }
                    });

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        btnDownQRProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("QRProduct").whereEqualTo("idproduct", product.getId())
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot q : queryDocumentSnapshots){
                            if (q.getString("idproduct").equals(product.getId())){
                                String getUrl = q.getString("hinhanh_qr");
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(getUrl));
                                String title = URLUtil.guessFileName(getUrl, null, null);
                                request.setTitle(title);
                                request.setDescription("Đang tải File, vui lòng đợi....");
                                String cookie = CookieManager.getInstance().getCookie(getUrl);
                                request.addRequestHeader("cookie", cookie);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);

                                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                downloadManager.enqueue(request);
                                Toast.makeText(AdminAddSPActivity.this, "Đã tải mã QR, kiểm tra trong thư viện ảnh", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }
        });
    }

    private void Init() {
        db = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        list.add("Chọn Danh mục");

        // Nhận data từ AdminProductActivity
        if (getIntent() != null && getIntent().hasExtra("SP")) {
            product = (Product) getIntent().getSerializableExtra("SP");
        }
        if (product != null) {
            btnQRProduct.setVisibility(View.VISIBLE);
            imgQRProduct.setVisibility(View.VISIBLE);
            tvTaoMaQR.setVisibility(View.VISIBLE);
            db.collection("QRProduct").whereEqualTo("idproduct", product.getId())
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot q : queryDocumentSnapshots){
                        if (q.getString("idproduct").equals(product.getId())){
                            btnQRProduct.setVisibility(View.GONE);
                            btnDownQRProduct.setVisibility(View.VISIBLE);
                            Picasso.get().load(q.getString("hinhanh_qr")).into(imgQRProduct);
                            tvTaoMaQR.setText("Mã QR sản phẩm");
                        }
                    }
                }
            });

            edtchatlieuSP.setText(product.getchatlieu());
            edtMotaSP.setText(product.getMota());
            edtsizeSP.setText(product.getsize());
            edtSoluongSP.setText(product.getSoluong() + "");
            edtGiatienSP.setText(product.getGiatien() + "");
            edtTenSP.setText(product.getTensp());
            edtTypeSP.setText(product.getType()+"");
            btnEdit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
            tvTitle.setText("Edit sản phẩm");
            if (!TextUtils.isEmpty(product.getHinhanh())) {
                Picasso.get().load(product.getHinhanh()).into(imgAdd);
                image = product.getHinhanh();
            }
        }

        db.collection("LoaiProduct").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                    list.add(q.getString("tenloai"));
                    Log.d("TAG", "onSuccess: " + q.getString("tenloai"));
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(AdminAddSPActivity.this, android.R.layout.simple_list_item_1, list);
                spinnerDanhMuc.setAdapter(arrayAdapter);
                if (list.size() > 0) {
                    spinnerDanhMuc.setSelection(1);
                    if (product != null) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).equals(product.getLoaisp())) {
                                spinnerDanhMuc.setSelection(i);
                                break;
                            }
                        }
                    }
                }

            }
        });

    }

    private boolean validate() {
        if (TextUtils.isEmpty(image)) {
            Toast.makeText(this, "Vui lòng chọn hình ảnh", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(edtGiatienSP.getText().toString())) {
            Toast.makeText(this, "Vui lòng nhập giá tiền", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(edtTenSP.getText().toString())) {
            Toast.makeText(this, "Vui lòng nhập tên sản phẩm", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(edtsizeSP.getText().toString())) {
            Toast.makeText(this, "Vui lòng nhập Size", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(edtchatlieuSP.getText().toString())) {
            Toast.makeText(this, "Vui lòng nhập Chất liệu", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(edtSoluongSP.getText().toString())) {
            Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(edtTypeSP.getText().toString())) {
            Toast.makeText(this, "Vui lòng nhập type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(edtMotaSP.getText().toString())) {
            Toast.makeText(this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void InitWidget() {
        btnDownQRProduct = findViewById(R.id.btn_down_qr_product);
        tvTaoMaQR = findViewById(R.id.tv_taomaqr);
        imgQRProduct = findViewById(R.id.img_qr_product);
        btnQRProduct = findViewById(R.id.btn_qr_product);
        tvTitle = findViewById(R.id.tv_title);
        imgAddLoaiProduct = findViewById(R.id.img_add_loaiproduct);
        btnAddBack = findViewById(R.id.btn_add_back);
        btnRefresh = findViewById(R.id.btn_refresh);
        btnSave = findViewById(R.id.btn_save);
        edtTenSP = findViewById(R.id.edt_tensp);
        edtGiatienSP = findViewById(R.id.edt_giatiensp);
        edtsizeSP = findViewById(R.id.edt_sizesp);
        edtchatlieuSP = findViewById(R.id.edt_chatlieusp);
        edtSoluongSP = findViewById(R.id.edt_soluongsp);
        edtTypeSP = findViewById(R.id.edt_typesp);
        edtMotaSP = findViewById(R.id.edt_motasp);
        imgAdd = findViewById(R.id.image_add);
        btnDanhmuc = findViewById(R.id.btn_danhmuc);
        btnDelete = findViewById(R.id.btn_delete);
        btnEdit = findViewById(R.id.btn_edit);
        spinnerDanhMuc = findViewById(R.id.spinner_danhmuc);

        // Dialog
        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Upload image");
        dialog.setMessage("Uploading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
    }

    private void pickImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    LIBRARY_PICKER);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), LIBRARY_PICKER);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // pick image after request permission success
            pickImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LIBRARY_PICKER && resultCode == RESULT_OK && null != data) {
            try {

                dialog.show();
                Uri uri = data.getData();
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] datas = baos.toByteArray();
                String filename = System.currentTimeMillis() + "";
                StorageReference storageReference;
                storageReference = FirebaseStorage.getInstance("gs://doan-dc57a.appspot.com/").getReference();
                storageReference.child("Profile").child(filename + ".jpg").putBytes(datas).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        if (taskSnapshot.getTask().isSuccessful()) {
                            storageReference.child("Profile").child(filename + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(@NonNull Uri uri) {
                                    imgAdd.setImageBitmap(bitmap);
                                    image = uri.toString();
                                }
                            });
                            Toast.makeText(AdminAddSPActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
            } catch (FileNotFoundException e) {
                Log.d("CHECKED", e.getMessage());
                dialog.dismiss();
            }

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (position > 0) {
            btnDanhmuc.setText(spinnerDanhMuc.getSelectedItem().toString());
            String s = list.get(position);
            loaisp = s;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}