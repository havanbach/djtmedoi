package com.example.djtmedoi.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.djtmedoi.Adapter.AllCommentAdapter;
import com.example.djtmedoi.Models.Binhluan;
import com.example.djtmedoi.Models.Product;
import com.example.djtmedoi.Presenter.BinhLuanPresenter;
import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.BinhLuanView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity implements BinhLuanView {

    private ImageView imgBackCMT;
    private TextView tvRatingTotal;
    private RatingBar ratingBarTotal;
    private TextView tvSoNguoiDanhGia;
    private ProgressBar progressBarFive, progressBarFour, progressBarThree, progressBarTwo, progressBarOne;
    private RecyclerView rcvAllCMT;

    private BinhLuanPresenter binhLuanPresenter;
    private ArrayList<Binhluan> mListCMT;
    private AllCommentAdapter adapter;
    private Product product;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    float rate;
    int avr = 0;
    float y1 = 0, y2 = 0, y3 = 0, y4 = 0, y5 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        InitWidget();
        Init();
        Event();
    }

    private void Init() {
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("allcmt");
        mListCMT = new ArrayList<>();
        binhLuanPresenter = new BinhLuanPresenter(this);
        binhLuanPresenter.HandleGetAllBinhLuan(product.getId());

        db.collection("BinhLuan").whereEqualTo("idproduct", product.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot q : queryDocumentSnapshots){
                    float x = Float.parseFloat(q.getString("rate"));

                    if (x <= 1){
                        y1 += Float.parseFloat(q.getString("rate"));
                    } else if (x > 1 && x <= 2){
                        y2 += Float.parseFloat(q.getString("rate"));
                    } else if (x > 2 && x <= 3){
                        y3 += Float.parseFloat(q.getString("rate"));
                    } else if (x > 3 && x <= 4){
                        y4 += Float.parseFloat(q.getString("rate"));
                    } else y5 += Float.parseFloat(q.getString("rate"));
                    rate += Float.parseFloat(q.getString("rate"));
                    avr++;
                }
                tvRatingTotal.setText(rate/avr+"");
                tvSoNguoiDanhGia.setText(avr+"");
                ratingBarTotal.setRating(rate/avr);

                progressBarOne.setProgress((int) y1);
                progressBarTwo.setProgress((int) y2);
                progressBarThree.setProgress((int) y3);
                progressBarFour.setProgress((int) y4);
                progressBarFive.setProgress((int) y5);


                Log.d("rate", "Số 1: " + y1 + "Số 2: " + y2 + "Số 3: " + y3 + "Số 4: " + y4 + "Số 5: " + y5);

            }
        });
    }



    private void Event() {
        imgBackCMT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void InitWidget() {

        imgBackCMT = findViewById(R.id.img_back_cmt);
        tvRatingTotal = findViewById(R.id.tv_rating_total);
        ratingBarTotal = findViewById(R.id.rating_bar_total);
        tvSoNguoiDanhGia = findViewById(R.id.tv_songuoi_danhgia);
        progressBarFive = findViewById(R.id.progress_bar_five);
        progressBarFour = findViewById(R.id.progress_bar_four);
        progressBarThree = findViewById(R.id.progress_bar_three);
        progressBarTwo = findViewById(R.id.progress_bar_two);
        progressBarOne = findViewById(R.id.progress_bar_one);
        rcvAllCMT = findViewById(R.id.rcv_all_cmt);
        ratingBarTotal.setIsIndicator(true);    //Không cho phép thay đổi giá trị ratingbar

    }

    @Override
    public void getDataBinhLuan(String idbinhluan, String idproduct, String iduser, String rate, String noidung) {
        mListCMT.add(new Binhluan(idbinhluan, idproduct, iduser, rate, noidung));
        adapter = new AllCommentAdapter(CommentActivity.this, mListCMT);
        rcvAllCMT.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rcvAllCMT.setAdapter(adapter);
    }
}