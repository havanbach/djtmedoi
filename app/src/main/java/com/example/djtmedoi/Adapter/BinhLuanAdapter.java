package com.example.djtmedoi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.djtmedoi.Models.Binhluan;
import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.IClickCTHD;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BinhLuanAdapter extends RecyclerView.Adapter<BinhLuanAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Binhluan> mlist;
    private IClickCTHD iClickCTHD;

    public BinhLuanAdapter(Context context, ArrayList<Binhluan> mlist, IClickCTHD iClickCTHD) {
        this.context = context;
        this.mlist = mlist;
        this.iClickCTHD = iClickCTHD;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dong_binhluan, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        Binhluan binhluan = mlist.get(position);
        holder.tvBinhLuan.setText(binhluan.getNoidung());
        holder.tvRate.setText(binhluan.getRate());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User").document(binhluan.getIduser()).collection("Profile").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot q : queryDocumentSnapshots){
                    holder.tvNameBinhLuan.setText(q.getString("hoten"));
                    Picasso.get().load(q.getString("avatar")).into(holder.cirHinhAnhBinhLuan);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView cirHinhAnhBinhLuan;
        private TextView tvBinhLuan, tvRate, tvNameBinhLuan;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            cirHinhAnhBinhLuan = itemView.findViewById(R.id.cir_hinhanh_binhluan);
            tvBinhLuan = itemView.findViewById(R.id.tv_binhluan);
            tvRate = itemView.findViewById(R.id.tv_rate);
            tvNameBinhLuan = itemView.findViewById(R.id.tv_name_binhluan);
        }
    }
}
