package com.example.djtmedoi.View;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.djtmedoi.Adapter.GiohangAdapter;
import com.example.djtmedoi.Models.Chat;
import com.example.djtmedoi.Models.Product;
import com.example.djtmedoi.Presenter.GioHangPresenter;
import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.GioHangView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import vn.momo.momo_partner.AppMoMoLib;

public class CartActivity extends AppCompatActivity implements GioHangView {

    private ScrollView scrollViewCart;
    private TextView tvNullCart;
    private View view;

    private RecyclerView rcvGioHang;
    private GiohangAdapter giohangAdapter;
    private GioHangPresenter gioHangPresenter;
    public ArrayList<Product> listGiohang;
    private  TextView tvDongia, tvPhiVanChuyen, tvTongTien;
    private TextView btnThanhToan;
    private ImageView imgBackCart;

    private Intent intent;
    private Product product;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Hiển thị dialog thanh toán
    private Spinner spinnerPhuongthuc;
    private  String s[]={"Thanh toán khi nhận hàng","Thanh toán MOMO"};
    private String tienthanhtoan = "";
    private String hoten, diachi, sdt, ghichu;
    private String ngaydat, phuongthuc;

    // Momo
    int total;
    Chat chat;
    Number number;

    String sanpham = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        InitWidget();
        DeleteDataGioHang();
        TongTienGioHang();
        Event();
    }

    private void InitWidget() {

        scrollViewCart = findViewById(R.id.scrollView_cart);
        tvNullCart = findViewById(R.id.tv_null_cart);
        rcvGioHang = findViewById(R.id.rcv_giohang);
        tvDongia = findViewById(R.id.tv_dongia);
        tvPhiVanChuyen = findViewById(R.id.tv_phivanchuyen);
        tvTongTien = findViewById(R.id.tv_tongtien);
        btnThanhToan = findViewById(R.id.btn_thanhtoan);
        imgBackCart = findViewById(R.id.img_back_cart);

        listGiohang = new ArrayList<>();
        gioHangPresenter = new GioHangPresenter(CartActivity.this);
        gioHangPresenter.HandlegetDataGioHang();



    }

    private void Event() {
        btnThanhToan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listGiohang.size()>0){
                    DiaLogThanhToan();
                }else{
                    Toast.makeText(CartActivity.this, "Giỏ hàng của bạn đang trống !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgBackCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void DiaLogThanhToan() {
        Dialog dialog = new Dialog(CartActivity.this);
        dialog.setContentView(R.layout.dialog_thanhtoan);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CustomInit(dialog);
    }
    private void CustomInit(Dialog dialog) {
        spinnerPhuongthuc = dialog.findViewById(R.id.spinner_phuongthuc);
        EditText edthoten = dialog.findViewById(R.id.edt_hoten);
        EditText edtdiachi = dialog.findViewById(R.id.edt_diachi);
        EditText edtsdt = dialog.findViewById(R.id.edt_sdt);
        EditText edtghichu = dialog.findViewById(R.id.edt_ghichu);
        TextView tvtongtien = dialog.findViewById(R.id.tv_tongtien);
        Button btnxacnhan = dialog.findViewById(R.id.btn_xacnhan);
        ImageView btnCancel = dialog.findViewById(R.id.btn_cancel);
        dialog.setCanceledOnTouchOutside(false);


        ArrayAdapter arrayAdapter = new ArrayAdapter(CartActivity.this,
                android.R.layout.simple_list_item_1, s);
        spinnerPhuongthuc.setAdapter(arrayAdapter);

        // Set info dialog
        tienthanhtoan = tvTongTien.getText().toString().trim();
        db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Profile").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size()>0) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    if(documentSnapshot!=null){
                        edthoten.setText(documentSnapshot.getString("hoten"));
                        edtdiachi.setText(documentSnapshot.getString("diachi"));
                        edtsdt.setText(documentSnapshot.getString("sdt"));
                    }
                }
            }
        });
        tvtongtien.setText(tienthanhtoan);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        btnxacnhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Product product: listGiohang){
                    sanpham += product.getTensp() +  " x " + product.getSoluong() + "\n";
                }
                hoten = edthoten.getText().toString().trim();
                diachi = edtdiachi.getText().toString().trim();
                sdt = edtsdt.getText().toString().trim();
                ghichu = edtghichu.getText().toString().trim();
                if(hoten.length()>0){
                    if(diachi.length()>0){
                        if(sdt.length()>0){
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Calendar calendar = Calendar.getInstance();
                            ngaydat = simpleDateFormat.format(calendar.getTime());
                            phuongthuc = spinnerPhuongthuc.getSelectedItem().toString();
                            switch (spinnerPhuongthuc.getSelectedItemPosition()){
                                case 0:

//                                    gioHangPresenter.HandleAddHoaDon(ghichu,simpleDateFormat.format(calendar.getTime()),diachi,hoten,sdt,spinnerPhuongthuc.getSelectedItem().toString(),tienthanhtoan,listGiohang);

                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("ghichu", ghichu);
                                    hashMap.put("ngaydat",ngaydat);
                                    hashMap.put("diachi",diachi);
                                    hashMap.put("sdt",sdt);
                                    hashMap.put("hoten",hoten);
                                    hashMap.put("phuongthuc",phuongthuc);
                                    hashMap.put("tongtien",tienthanhtoan);
                                    hashMap.put("trangthai",1);
                                    hashMap.put("UID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    db.collection("HoaDon")
                                            .add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if(task.isSuccessful()){
                                                String idhoadon = task.getResult().getId();
                                                for(Product sanPhamModels : listGiohang){
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
//                                                                                        DatHangGuiTinNhan();
                                                                                        listGiohang.clear();
                                                                                        giohangAdapter.notifyDataSetChanged();
                                                                                        TongTienGioHang();
                                                                                        scrollViewCart.setVisibility(View.INVISIBLE);
                                                                                        tvNullCart.setVisibility(View.VISIBLE);
//                                                                                        dialog.cancel();

                                                                                        Log.d("idhoadon", "ID hóa đơn là: " + idhoadon);
                                                                                    } else {
                                                                                        Toast.makeText(CartActivity.this, "Thất bại", Toast.LENGTH_SHORT).show();
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



                                                Intent intent = new Intent(CartActivity.this, OrderSuccessActivity.class);
                                                intent.putExtra("idhoadon", idhoadon);
                                                intent.putExtra("hoten", hoten);
                                                intent.putExtra("diachi", diachi);
                                                intent.putExtra("sdt", sdt);
                                                intent.putExtra("ghichu", ghichu);
                                                intent.putExtra("ngaydat", ngaydat);
                                                intent.putExtra("phuongthuc", phuongthuc);
                                                intent.putExtra("tienthanhtoan", tienthanhtoan);
                                                intent.putExtra("sanpham", sanpham);
                                                intent.putExtra("serialzable",listGiohang);
                                                startActivity(intent);
                                                finish();
                                            }else{

                                            }

                                        }
                                    });
                                    DatHangGuiTinNhan();

                                    break;
                                case 1:

//                                    gioHangPresenter.HandleAddHoaDon(ghichu,simpleDateFormat.format(calendar.getTime()),diachi,hoten,sdt,spinnerPhuongthuc.getSelectedItem().toString(),tienthanhtoan,listGiohang);
                                    dialog.cancel();
                                    requestPayment();
                                    DatHangGuiTinNhan();
                                    break;
                            }

                        }else{
                            Toast.makeText(CartActivity.this, "Số điện thoại không để trống", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(CartActivity.this, "Địa chỉ không để trống", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CartActivity.this, "Họ tên không để trống", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public  void TongTienGioHang() {
        int tongtien = 0;
        tvPhiVanChuyen.setText(String.valueOf(10000));
        int phi = Integer.parseInt(tvPhiVanChuyen.getText().toString());
        for (Product product: listGiohang){
            tongtien += product.getGiatien() * product.getSoluong();
        }
        tvDongia.setText(String.valueOf(tongtien));
        int dongia = Integer.parseInt(tvDongia.getText().toString());
        tvTongTien.setText(NumberFormat.getInstance().format(phi + dongia));
    }
    private void DeleteDataGioHang(){
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return true;
            }
            //chức năng xóa sp trong giỏ hàng
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                AlertDialog.Builder buidler = new AlertDialog.Builder(CartActivity.this);
                buidler.setMessage("Bạn có muốn xóa  sản phẩm " + listGiohang.get(pos).getTensp() + " không?");
                buidler.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gioHangPresenter.HandleDeleteDataGioHang(listGiohang.get(pos).getId());
                        listGiohang.remove(pos);
                        TongTienGioHang();
                        giohangAdapter.notifyDataSetChanged();
                        if (listGiohang.size() == 0){
                            scrollViewCart.setVisibility(View.INVISIBLE);
                            tvNullCart.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(CartActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();

                    }
                });
                buidler.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        giohangAdapter.notifyDataSetChanged();
                    }
                });
                buidler.show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rcvGioHang);
    }
    public void DatHangGuiTinNhan(){


        // Realtime Database
        // Khi đặt hàng xong thì người dùng sẽ được gửi 1 tin nhắn tự động từ tk admin
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> map = new HashMap<>();
        map.put("sender", "onqxIKWN2niMfKCraNYw");  // Người gửi đang fix cứng là id của admin
        map.put("receiver", FirebaseAuth.getInstance().getCurrentUser().getUid());
        String donhang = "";
        for (Product product: listGiohang){
            donhang += product.getTensp() + " x " + product.getSoluong() + "\n";
        }
        String mess = "Đơn hàng của bạn: " + "\n" + donhang + "\n" + "Ngày đặt: " + ngaydat + "\n" + "Địa chỉ: " + diachi + "\n" + "SĐT: " + sdt
                + "\n" + "Người nhận: " + hoten + "\n" + "Phương thức thanh toán: " + phuongthuc
                + "\n" + "Tổng tiền: " + tienthanhtoan;
        map.put("message", mess);
        map.put("isseen", false);

        reference.child("Chats").push().setValue(map);

        // Add user to chat fragment
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("onqxIKWN2niMfKCraNYw");

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef.child("id").setValue("onqxIKWN2niMfKCraNYw");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void OnSucess() {

    }

    @Override
    public void OnFail() {

    }

    @Override
    public void getDataSanPham(String id, String idsp, String tensp, Long giatien, String hinhanh, String loaisp, String mota, Long soluong, String size, Long type, String chatlieu) {
        try{
            listGiohang.add(new Product(id,idsp,tensp,giatien,hinhanh,loaisp,mota,soluong,size,type,chatlieu));
            product = new Product(id,idsp,tensp,giatien,hinhanh,loaisp,mota,soluong,size,type,chatlieu);
            Log.d("product", "Sản phẩm: " + product.getId() + product.getTensp() + product.getSoluong() + product.getGiatien());

            if (listGiohang.size() != 0){
                scrollViewCart.setVisibility(View.VISIBLE);
                tvNullCart.setVisibility(View.GONE);
            } else {
                scrollViewCart.setVisibility(View.GONE);
                tvNullCart.setVisibility(View.VISIBLE);
            }
            giohangAdapter = new GiohangAdapter(CartActivity.this,listGiohang, CartActivity.this);
            rcvGioHang.setLayoutManager(new LinearLayoutManager(CartActivity.this));
            rcvGioHang.setAdapter(giohangAdapter);
            TongTienGioHang();
        }catch (Exception e){

        }
    }

    //Thanh toán momo
    private void requestPayment() {

        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT);
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.MAP);
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);
        Map<String, Object> eventValue = new HashMap<>();
        //client Required
        long mahd =   System.currentTimeMillis();
        try {
            number = NumberFormat.getInstance().parse(tienthanhtoan);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        eventValue.put("merchantname", "Afforda Company - Nguyen Van Chinh"); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue.put("merchantcode", "MOMO1NRV20220112"); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue.put("amount", Integer.parseInt(String.valueOf(number))); //Kiểu integer
        eventValue.put("orderId", "order"+mahd); //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
        eventValue.put("orderLabel", "Mã đơn hàng"); //gán nhãn

        //client Optional - bill info
        eventValue.put("merchantnamelabel", "Dịch vụ");//gán nhãn

        eventValue.put("fee", Integer.parseInt(String.valueOf(number))); //Kiểu integer
        eventValue.put("description", "Mô tả"); //mô tả đơn hàng - short description

        //client extra data
        eventValue.put("requestId",  "MOMO1NRV20220112"+"merchant_billId_"+System.currentTimeMillis());
        eventValue.put("partnerCode", "MOMO1NRV20220112");
        Log.d("end", "end1");
        //Example extra data
        JSONObject objExtraData = new JSONObject();
        try {
            objExtraData.put("site_code", "008");
            objExtraData.put("site_name", "Thanh Toán Food");
            objExtraData.put("screen_code", 0);
            objExtraData.put("screen_name", "Đặc Biệt");
            String name ="";
            for(Product sanPham : listGiohang){
                name+=sanPham.getTensp()+",";

            }
            objExtraData.put("movie_name", name);
            objExtraData.put("movie_format", "Đồ ăn");
            Log.d("end", "end2");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("end", "Lỗi: " + e);
        }
        eventValue.put("extraData", objExtraData.toString());
        eventValue.put("extra", "");
        Log.d("end", "end3");
        AppMoMoLib.getInstance().requestMoMoCallBack(CartActivity.this, eventValue);
        Log.d("end", "end4");

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("CHECKED","checked1");
        if(requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            Log.d("CHECKED","checked2");
            if(data != null) {
                Log.d("CHECKED","checked3");
                if(data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
                    Log.d("Messagesss","message: " + "Get token " + data.getStringExtra("message"));
                    String checked = data.getStringExtra("message");
                    Log.d("CHECKED",checked);
                    Calendar calendar=Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
//                    gioHangPresenter.HandleAddHoaDon(ghichu,simpleDateFormat.format(calendar.getTime()),diachi,hoten,sdt,spinnerPhuongthuc.getSelectedItem().toString(),tienthanhtoan,listGiohang);
                    HashMap<String,Object> hashMap2 = new HashMap<>();
                    hashMap2.put("ghichu", ghichu);
                    hashMap2.put("ngaydat",ngaydat);
                    hashMap2.put("diachi",diachi);
                    hashMap2.put("sdt",sdt);
                    hashMap2.put("hoten",hoten);
                    hashMap2.put("phuongthuc",phuongthuc);
                    hashMap2.put("tongtien",tienthanhtoan);
                    hashMap2.put("trangthai",1);
                    hashMap2.put("UID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    db.collection("HoaDon")
                            .add(hashMap2).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                String idhoadon = task.getResult().getId();
                                for(Product sanPhamModels : listGiohang){
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
                                                                        listGiohang.clear();
                                                                        giohangAdapter.notifyDataSetChanged();
                                                                        TongTienGioHang();
                                                                        scrollViewCart.setVisibility(View.INVISIBLE);
                                                                        tvNullCart.setVisibility(View.VISIBLE);
                                                                        Log.d("idhoadon", "ID hóa đơn là: " + idhoadon);
                                                                    } else {
                                                                        Toast.makeText(CartActivity.this, "Thất bại", Toast.LENGTH_SHORT).show();
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
                                Intent intent = new Intent(CartActivity.this, OrderSuccessActivity.class);
                                intent.putExtra("idhoadon", idhoadon);
                                intent.putExtra("hoten", hoten);
                                intent.putExtra("diachi", diachi);
                                intent.putExtra("sdt", sdt);
                                intent.putExtra("ghichu", ghichu);
                                intent.putExtra("ngaydat", ngaydat);
                                intent.putExtra("phuongthuc", phuongthuc);
                                intent.putExtra("tienthanhtoan", tienthanhtoan);
                                intent.putExtra("sanpham", sanpham);
                                intent.putExtra("serialzable",listGiohang);
                                startActivity(intent);
                                finish();
                            }else{

                            }

                        }
                    });


                    String token = data.getStringExtra("data"); //Token response
                    String phoneNumber = data.getStringExtra("phonenumber");
                    String env = data.getStringExtra("env");
                    if(env == null){
                        env = "app";
                    }

                    if(token != null && !token.equals("")) {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        // IF Momo topup success, continue to process your order
                    } else {
                        Log.d("Message Error : ","message: " + "Get token " + data.getStringExtra("message"));

                    }
                } else if(data.getIntExtra("status", -1) == 1) {
                    String message = data.getStringExtra("message") != null?data.getStringExtra("message"):"Thất bại";
                    Log.d("Message Fail : ","message: " + "Get token " + data.getStringExtra("message"));
                } else if(data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
                    Log.d("Message Fail 1 : ","message: " + "Get token " + data.getStringExtra("message"));
                } else {
                    //TOKEN FAIL
                    Log.d("Message Fail 2 : ","message: " + "Get token " + data.getStringExtra("message"));
                }
            } else {
                Log.d("Message Fail 3 : ","message: " + "Get token " + data.getStringExtra("message"));
            }
        } else {
            Log.d("Message Fail 4 : ","message: " + "Get token " + data.getStringExtra("message"));
        }
    }
}