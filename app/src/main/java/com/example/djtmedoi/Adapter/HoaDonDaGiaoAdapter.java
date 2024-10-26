package com.example.djtmedoi.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.djtmedoi.Models.Product;
import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.IClickCTHD;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HoaDonDaGiaoAdapter extends RecyclerView.Adapter<HoaDonDaGiaoAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Product> list;
    private IClickCTHD iClickCTHD;
    private int type;

    public void setDataHoaDonDaGiao(Context context, ArrayList<Product> list, int type, IClickCTHD iClickCTHD) {
        this.context = context;
        this.list = list;
        this.type = type;
        this.iClickCTHD = iClickCTHD;
        notifyDataSetChanged();

    }

//    public Boolean TrangThaiDaGiao(boolean b){
//        return b;
//    }




    @NotNull
    @Override
    public HoaDonDaGiaoAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        if (type == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dong_hoadon_dagiao, parent, false);
        } else view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dong_hoadon_khac, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HoaDonDaGiaoAdapter.ViewHolder holder, int position) {
        Log.d("abc", "onBind");
        Product product = list.get(position);
        holder.tvTen.setText(product.getTensp());
        holder.tvsize.setText(product.getsize());
        holder.tvSoluong.setText(product.getSoluong() + "");
        holder.tvDongia.setText(NumberFormat.getInstance().format(product.getGiatien()));
        holder.tvTotal.setText(NumberFormat.getInstance().format(product.getGiatien() * product.getSoluong()));
        Picasso.get().load(product.getHinhanh()).into(holder.img);

//        holder.constraintDongDanhGia.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                iClickCTHD.onClickCTHD(position);
//            }
//        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("BinhLuan").whereEqualTo("iduser", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot q : queryDocumentSnapshots){
                    if (product.getIdsp().equals(q.getString("idproduct"))){
                        holder.btnDongDanhGia.setEnabled(false);
                        holder.btnDongDanhGia.setText("Đã đánh giá");
                    }
                }

            }
        });

        holder.btnDongDanhGia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickCTHD.onClickCTHD(position);
            }
        });


    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout constraintDongDanhGia;
        private TextView tvTen, tvsize, tvSoluong, tvDongia, tvTotal;
        private CircleImageView img;
        private Button btnDongDanhGia;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvTen = itemView.findViewById(R.id.tv_name_dong_danhgia);
            tvsize = itemView.findViewById(R.id.tv_size_dong_danhgia);
            tvSoluong = itemView.findViewById(R.id.tv_number_dong_danhgia);
            tvDongia = itemView.findViewById(R.id.tv_giatien_dong_danhgia);
            tvTotal = itemView.findViewById(R.id.tv_total_dong_danhgia);
            img = itemView.findViewById(R.id.img_dong_danhgia);
            constraintDongDanhGia = itemView.findViewById(R.id.constraint_dong_danhgia);
            btnDongDanhGia = itemView.findViewById(R.id.btn_dong_danhgia);
        }
    }
}
