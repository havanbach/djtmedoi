package com.example.djtmedoi.View.Admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.djtmedoi.Models.Product;
import com.example.djtmedoi.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;

public class AdminCTHDAdapter extends RecyclerView.Adapter<AdminCTHDAdapter.ViewHolder> {
    private ArrayList<Product> list;

    public void setData(ArrayList<Product> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public AdminCTHDAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dong_chitiet_hoadon,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdminCTHDAdapter.ViewHolder holder, int position) {
        Product product = list.get(position);
        holder.tvTenCTHD.setText(product.getTensp());
        holder.tvsizeCTHD.setText(product.getsize());
        holder.tvSoluongCTHD.setText(product.getSoluong()+"");
        holder.tvDongiaCTHD.setText(NumberFormat.getInstance().format(product.getGiatien()));
        holder.tvTotalCTHD.setText(NumberFormat.getInstance().format(product.getGiatien() * product.getSoluong()));
        Picasso.get().load(product.getHinhanh()).into(holder.imgCTHD);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvTenCTHD, tvsizeCTHD, tvSoluongCTHD, tvDongiaCTHD, tvTotalCTHD;
        private ImageView imgCTHD;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvTenCTHD = itemView.findViewById(R.id.tv_ten_cthd);
            tvsizeCTHD = itemView.findViewById(R.id.tv_size_cthd);
            tvSoluongCTHD = itemView.findViewById(R.id.tv_number_cthd);
            tvDongiaCTHD = itemView.findViewById(R.id.tv_giatien_cthd);
            tvTotalCTHD = itemView.findViewById(R.id.tv_total_cthd);
            imgCTHD = itemView.findViewById(R.id.img_cthd);
        }
    }

}
