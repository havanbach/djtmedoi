package com.example.djtmedoi.View.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.djtmedoi.Models.Product;
import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.IClickCTHD;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Product> mlist;
    private IClickCTHD iClickCTHD;

    public AdminProductAdapter(Context context, ArrayList<Product> mlist, IClickCTHD iClickCTHD) {
        this.context = context;
        this.mlist = mlist;
        this.iClickCTHD = iClickCTHD;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dong_admin_product,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        Product product = mlist.get(position);
        holder.tvTen.setText(product.getTensp());
        holder.tvMota.setText(product.getMota());
        holder.tvSoluong.setText(product.getSoluong()+"");
        holder.tvDongia.setText(NumberFormat.getInstance().format(product.getGiatien()));
        Picasso.get().load(product.getHinhanh()).into(holder.img);

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickCTHD.onClickCTHD(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvTen, tvMota, tvSoluong, tvDongia;
        private ImageView img;
        private ConstraintLayout constraintLayout;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvTen = itemView.findViewById(R.id.tv_ten_admin);
            tvMota = itemView.findViewById(R.id.tv_mota_admin);
            tvSoluong = itemView.findViewById(R.id.tv_number_admin);
            tvDongia = itemView.findViewById(R.id.tv_dongia_admin);
            img = itemView.findViewById(R.id.img_admin);
            constraintLayout = itemView.findViewById(R.id.constraint_item_admin_product);
        }
    }
}
