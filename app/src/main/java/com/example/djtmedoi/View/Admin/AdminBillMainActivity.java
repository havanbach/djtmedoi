package com.example.djtmedoi.View.Admin;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.djtmedoi.Adapter.HoaDonAdapter;
import com.example.djtmedoi.Models.HoaDon;
import com.example.djtmedoi.Presenter.HoaDonPreSenter;
import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.HoaDonView;
import com.example.djtmedoi.my_interface.IClickCTHD;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminBillMainActivity extends AppCompatActivity implements HoaDonView, AdapterView.OnItemSelectedListener {

    private Toolbar toolbarBillAdmin;
    private RecyclerView rcvBillAdmin;
    private Spinner spinnerStatusAdmin, spinnerUserAdmin;
    private String[] strStatus = {"Tất cả", "Đang xử lý", "Đang giao hàng", "Đã giao", "Đã hủy"};

    private HoaDonPreSenter hoaDonPreSenter;
    private ArrayList<HoaDon> mListStatus;
    private ArrayList<String> mlistUser;
    private ArrayList<HoaDon> mListFilter;
    private HoaDonAdapter adapter;

    public int positionStatus;
    private String iduser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_bill_main);
        InitWidget();
        Init();
        Event();
    }



    private void Event() {
        toolbarBillAdmin.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


//        Log.d("position", "iduser là: " + iduser + " + " + "trạng thái là: " + positionStatus);
//        Log.d("position","trạng thái là: " + positionStatus);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int pos = viewHolder.getAdapterPosition();
                DiaLogUpDate(pos);


            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rcvBillAdmin);
    }

    private void InitWidget() {
        toolbarBillAdmin = findViewById(R.id.toolbar_bill_admin);
        rcvBillAdmin = findViewById(R.id.rcv_bill_admin);
        spinnerStatusAdmin = findViewById(R.id.spinner_status_admin);
        spinnerUserAdmin = findViewById(R.id.spinner_user_admin);
        mlistUser = new ArrayList<>();
        mListStatus = new ArrayList<>();
        mListFilter = new ArrayList<>();


    }

    private void Init() {
        setSupportActionBar(toolbarBillAdmin);
        getSupportActionBar().setTitle("Back");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hoaDonPreSenter = new HoaDonPreSenter(this);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, strStatus);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatusAdmin.setAdapter(arrayAdapter);
        spinnerStatusAdmin.setOnItemSelectedListener(this);


        mlistUser.add("Tất cả");
        db.collection("IDUser").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot d : queryDocumentSnapshots){
                    mlistUser.add(d.getString("iduser"));
                    ArrayAdapter arrayAdapter2 = new ArrayAdapter(AdminBillMainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, mlistUser);
                    arrayAdapter2.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    spinnerUserAdmin.setAdapter(arrayAdapter2);
                    spinnerUserAdmin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            String s = mlistUser.get(i);
                            iduser = s;
                            Log.d("position", "iduser là: " + iduser);
                            Log.d("position","trạng thái là: " + positionStatus);

                            // Nếu spinnerUser, spinnerStatus đều có position = 0 thì show mlistFilter theo trạng thái:
                            if (positionStatus == 0 && iduser.equals("Tất cả")){
                                mListFilter.clear();
                                if (adapter!=null){
                                    adapter.notifyDataSetChanged();
                                }
                                hoaDonPreSenter.HandleReadDataHD(null, 0);
                            } else if (positionStatus == 0){
                                mListFilter.clear();
                                if (adapter!=null){
                                    adapter.notifyDataSetChanged();
                                }
                                hoaDonPreSenter.HandleReadDataHD(iduser, 0);
                                Log.d("listBill", mListFilter.size()+"");
                            } else if (iduser.equals("Tất cả")){
                                mListFilter.clear();
                                if (adapter!=null){
                                    adapter.notifyDataSetChanged();
                                }
                                hoaDonPreSenter.HandleReadDataHD(null, positionStatus);
                                Log.d("listBill", mListFilter.size()+"");
                            }
                            // Show mlistFilter theo trạng thái, theo iduser:
                            else {
                                mListFilter.clear();
                                if (adapter!=null){
                                    adapter.notifyDataSetChanged();
                                }
                                hoaDonPreSenter.HandleReadDataHD(iduser, positionStatus);
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

    private void DiaLogUpDate(int pos) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_update_trangthai);
        dialog.show();
//        adapter.notifyDataSetChanged();

        Spinner spiner = dialog.findViewById(R.id.spinerCapNhat);
        String[] s = {"Chọn Mục","Đang xử lý", "Đang giao hàng", "Đã giao", "Đã hủy"} ;
        ArrayAdapter arrayAdapter  = new ArrayAdapter(this, android.R.layout.simple_list_item_1,s);
        spiner.setAdapter( arrayAdapter);
        spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    hoaDonPreSenter.CapNhatTrangThai(position,mListFilter.get(pos).getId());
                    dialog.cancel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void getDataHD(String id, String uid, String ghichu, String diachi, String hoten, String ngaydat, String phuongthuc, String sdt, String tongtien, Long type) {
//        mListStatus.add(new HoaDon(id,uid,ghichu,diachi,hoten,ngaydat,phuongthuc,sdt,tongtien,type));
        mListFilter.add(new HoaDon(id,uid,ghichu,diachi,hoten,ngaydat,phuongthuc,sdt,tongtien,type));
        adapter = new HoaDonAdapter();
        adapter.setDataHoadon(mListFilter, new IClickCTHD() {
            @Override
            public void onClickCTHD(int pos) {
                HoaDon hoaDon = mListFilter.get(pos);
                Intent intent = new Intent(AdminBillMainActivity.this, AdminCTHDActivity.class);
                intent.putExtra("CTHD", hoaDon);
                startActivity(intent);
            }
        });
        rcvBillAdmin.setLayoutManager(new LinearLayoutManager(this));
        rcvBillAdmin.setAdapter(adapter);
        Log.d("sp", mListFilter.size() + "");
    }

    @Override
    public void OnFail() {

    }

    @Override
    public void OnSucess() {
        mListFilter.clear();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
        hoaDonPreSenter.HandleReadDataHD(null, 0);
        Toast.makeText(this, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        positionStatus = i;
        mListFilter.clear();
        if (adapter!=null){
            adapter.notifyDataSetChanged();
        }
        hoaDonPreSenter.HandleReadDataHD(iduser, positionStatus);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}