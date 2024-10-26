package com.example.djtmedoi.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.djtmedoi.Adapter.BinhLuanAdapter;
import com.example.djtmedoi.MainActivity;
import com.example.djtmedoi.Models.Binhluan;
import com.example.djtmedoi.Models.Product;
import com.example.djtmedoi.Presenter.BinhLuanPresenter;
import com.example.djtmedoi.Presenter.GioHangPresenter;
import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.BinhLuanView;
import com.example.djtmedoi.my_interface.GioHangView;
import com.example.djtmedoi.my_interface.IClickCTHD;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class DetailSPActivity extends AppCompatActivity implements BinhLuanView, GioHangView {

    private ImageView viewAnimation;
    private View viewEndAnimation;


    private AppCompatToggleButton toggleButtonFavorite;
    private FloatingActionButton btnAddCartDetail;
    private LinearLayout linearShowAllCmt;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView tvGiaDetail, tvsizeDetail, tvchatlieuDetail, tvMoTaDetail;
    private ImageView imgDetail;
    private Toolbar toolbar;
    private RecyclerView rcvBinhluan;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private BinhLuanPresenter binhLuanPresenter;
    private ArrayList<Binhluan> mListBinhluan;
    private BinhLuanAdapter adapter;
    private Product product;

    private BottomSheetDialog bottomSheetDialog;
    private TextView tvTenBottom, tvGiaBottom, tvSoluongBottom, tvMotaBottom;
    private ImageView imgBottom, btnMinusBottom, btnPlusBottom;
    private Button btnBottom;
    private int slBottom = 1;
    private GioHangPresenter gioHangPresenter;

    private MainActivity mainActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_spactivity);

        InitWidget();
        Init();
        Event();

    }

    private void Event() {
        linearShowAllCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailSPActivity.this, CommentActivity.class);
                intent.putExtra("allcmt", product);
                startActivity(intent);
            }
        });

        btnAddCartDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBottomSheetDialog();
                tvTenBottom.setText(product.getTensp());
                tvGiaBottom.setText(NumberFormat.getInstance().format(product.getGiatien())+"");
                tvMotaBottom.setText(product.getMota());
                Picasso.get().load(product.getHinhanh()).into(imgBottom);
                initBottomSheet();
                bottomSheetDialog.show();
            }
        });
    }

    // Nút yêu thích
    public void onDefaultToggleClick(View view) {
        final String[] idlove = {""};
        boolean ToggleButtonState = toggleButtonFavorite.isChecked();
        if (ToggleButtonState){
            // Nếu thích thì làm gì và ngược lại
            HashMap<String, String> map = new HashMap<>();
            map.put("idproduct", product.getId());
            map.put("iduser", FirebaseAuth.getInstance().getCurrentUser().getUid());
            db.collection("Favorite").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    idlove[0] = documentReference.getId();
                }
            });

        } else {
            db.collection("Favorite").whereEqualTo("idproduct", product.getId())
                    .whereEqualTo("iduser", FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot q : queryDocumentSnapshots){
                        db.collection("Favorite").document(q.getId()).delete();
                    }
                }
            });
        }

    }

    private void setBottomSheetDialog(){
        // Bottom sheet Dialog
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.layout_persistent_bottom_sheet);
        tvTenBottom = bottomSheetDialog.findViewById(R.id.tv_ten_bottom);
        imgBottom = bottomSheetDialog.findViewById(R.id.img_bottom);
        tvGiaBottom = bottomSheetDialog.findViewById(R.id.tv_gia_bottom);
        btnMinusBottom = bottomSheetDialog.findViewById(R.id.btn_minus_bottom);
        btnPlusBottom = bottomSheetDialog.findViewById(R.id.btn_plus_bottom);
        tvSoluongBottom = bottomSheetDialog.findViewById(R.id.tv_soluong_bottom);
        tvMotaBottom = bottomSheetDialog.findViewById(R.id.tv_mota_bottom);
        btnBottom = bottomSheetDialog.findViewById(R.id.btn_bottom);
    }
    public void initBottomSheet(){
        btnMinusBottom.setVisibility(View.GONE);
        btnMinusBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slBottom = Integer.parseInt(tvSoluongBottom.getText().toString()) - 1;
                tvSoluongBottom.setText(String.valueOf(slBottom));
//                btnPlusBottom.setVisibility(View.VISIBLE);    // Nếu muốn giới hạn lượng đặt sản phẩm
                if (slBottom < 2){
                    btnMinusBottom.setVisibility(View.GONE);
                } else btnMinusBottom.setVisibility(View.VISIBLE);
            }
        });
        btnPlusBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slBottom = Integer.parseInt(tvSoluongBottom.getText().toString()) + 1;
                tvSoluongBottom.setText(String.valueOf(slBottom));

                if (slBottom < 2){
                    btnMinusBottom.setVisibility(View.GONE);
                } else btnMinusBottom.setVisibility(View.VISIBLE);

                // Nếu muốn giới hạn lượng đặt sản phẩm
//                if (slBottom >= product.getSoluong()){
//                    btnPlusBottom.setVisibility(View.GONE);
//                    Toast.makeText(DetailSPActivity.this, "Sản phẩm đặt quá giới hạn cho phép", Toast.LENGTH_SHORT).show();
//                }

            }
        });

        btnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gioHangPresenter.AddCart(product.getId(), Long.valueOf(slBottom));
//                AnimationUtil.translateAnimation(viewAnimation, viewAnimation, viewEndAnimation, new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        gioHangPresenter.AddCart(product.getId(), Long.valueOf(slBottom));
////                        mainActivity.setCountProductInCart(mainActivity.getmCountProduct() + 1);
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//
//                    }
//                });
            }
        });
    }

    private void Init() {
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("search");

        // Check sp này đã được yêu thích từ trước hay chưa, nếu có thì sẽ set cho tooglebutton = true;
        db.collection("Favorite").whereEqualTo("iduser", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot q : queryDocumentSnapshots){
                    if (q.getString("idproduct").equals(product.getId())){
                        toggleButtonFavorite.setChecked(true);
                    }
                }
            }
        });

        collapsingToolbarLayout.setTitle(product.getTensp());
        tvGiaDetail.setText(NumberFormat.getInstance().format(product.getGiatien()));
        tvsizeDetail.setText(product.getsize());
        tvchatlieuDetail.setText(product.getchatlieu());
        tvMoTaDetail.setText(product.getMota());
        Picasso.get().load(product.getHinhanh()).into(imgDetail);


        Log.d("soluong", "Số lượng sp là: " + product.getSoluong());
        binhLuanPresenter = new BinhLuanPresenter(this);
        binhLuanPresenter.HandleGetBinhLuanLimit(product.getId());

    }

    private void InitWidget() {
        mainActivity = new MainActivity();
        viewAnimation = findViewById(R.id.view_animation);
        viewEndAnimation = findViewById(R.id.view_end_animation);
        toggleButtonFavorite = findViewById(R.id.toogle_btn_favorite);
        btnAddCartDetail = findViewById(R.id.btn_addcart_detail);
        linearShowAllCmt = findViewById(R.id.linear_show_all_cmt);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        tvGiaDetail = findViewById(R.id.tv_gia_detail);
        tvsizeDetail = findViewById(R.id.tv_size_detail);
        tvchatlieuDetail = findViewById(R.id.tv_chatlieu_detail);
        tvMoTaDetail = findViewById(R.id.tv_mota_detail);
        imgDetail = findViewById(R.id.img_detail);
        rcvBinhluan = findViewById(R.id.rcv_binhluan);
        toolbar = findViewById(R.id.toolbar_detail);

        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mListBinhluan = new ArrayList<>();
        gioHangPresenter = new GioHangPresenter(this);


    }

    @Override
    public void getDataBinhLuan(String idbinhluan, String idproduct, String iduser, String rate, String noidung) {
        mListBinhluan.add(new Binhluan(idbinhluan, idproduct, iduser, rate, noidung));
        adapter = new BinhLuanAdapter(DetailSPActivity.this, mListBinhluan, new IClickCTHD() {
            @Override
            public void onClickCTHD(int pos) {

            }
        });

        rcvBinhluan.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rcvBinhluan.setAdapter(adapter);
    }

    @Override
    public void OnSucess() {
        Toast.makeText(this, "Thêm giỏ hàng thành công", Toast.LENGTH_SHORT).show();
        bottomSheetDialog.dismiss();
    }

    @Override
    public void OnFail() {
        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getDataSanPham(String id, String idsp, String tensp, Long giatien, String hinhanh, String loaisp, String mota, Long soluong, String size, Long type, String chatlieu) {

    }
}