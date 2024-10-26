package com.example.djtmedoi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.djtmedoi.Models.Product;
import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.IClickOpenBottomSheet;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Product> arrayList;
    private  int type = 0;
    private IClickOpenBottomSheet iClickOpenBottomSheet;

    public ProductAdapter(Context context, ArrayList<Product> arrayList, int type, IClickOpenBottomSheet iClickOpenBottomSheet) {
        this.context = context;
        this.arrayList = arrayList;
        this.type = type;
        this.iClickOpenBottomSheet = iClickOpenBottomSheet;
    }

    public ProductAdapter(Context context, ArrayList<Product> arrayList, int type) {
        this.context = context;
        this.arrayList = arrayList;
        this.type = type;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        if (type == 1){
            view = LayoutInflater.from(context).inflate(R.layout.dong_ds_product, parent, false);
        } else if(type ==2){
            view = LayoutInflater.from(context).inflate(R.layout.dong_product_noibat,parent,false);
        }else if(type ==3){
            view = LayoutInflater.from(context).inflate(R.layout.dong_do_uong,parent,false);
        }else if(type ==4){
            view = LayoutInflater.from(context).inflate(R.layout.dong_sp_thoitrang1,parent,false);
        }else if(type ==5){
            view = LayoutInflater.from(context).inflate(R.layout.dong_sp_micay,parent,false);
        }else if(type ==6){
            view = LayoutInflater.from(context).inflate(R.layout.dong_sp_yeuthich,parent,false);
        }else if(type ==7){
            view = LayoutInflater.from(context).inflate(R.layout.dong_sp_lau,parent,false);
        }else if(type ==8){
            view = LayoutInflater.from(context).inflate(R.layout.dong_sp_goiy,parent,false);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.dong_giohang,parent,false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        Product product = arrayList.get(position);
        holder.tvTenProduct.setText(product.getTensp());
        holder.tvGiaProduct.setText(NumberFormat.getInstance().format(product.getGiatien())+"");
        Picasso.get().load(product.getHinhanh()).into(holder.imgProduct);

        if(type==0){
            holder.tvchatlieuProduct.setText(product.getchatlieu());
            holder.tvSoluongProduct.setText(product.getSoluong()+"");
        }

        holder.layoutProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickOpenBottomSheet.onClickOpenBottomSheet(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTenProduct, tvGiaProduct, tvchatlieuProduct, tvSoluongProduct;
        private ImageView imgProduct;
        private LinearLayout layoutProduct;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            // tên, giá, ảnh sẽ được ánh xạ chung giữa các view
            tvTenProduct = itemView.findViewById(R.id.tv_ten_product);
            tvGiaProduct = itemView.findViewById(R.id.tv_giatien_product);
            imgProduct = itemView.findViewById(R.id.img_product);

            tvchatlieuProduct = itemView.findViewById(R.id.tv_chatlieu_giohang);
            tvSoluongProduct = itemView.findViewById(R.id.tv_number_giohang);

            layoutProduct = itemView.findViewById(R.id.layout_product);
        }
    }
}
