package com.example.djtmedoi.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.djtmedoi.Models.LoaiProduct;
import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.IClickLoaiProduct;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LoaiProductAdapter extends RecyclerView.Adapter<LoaiProductAdapter.ViewHolder>{
    private List<LoaiProduct> mList;
    private IClickLoaiProduct iClickLoaiProduct;
    public void setData(List<LoaiProduct> list, IClickLoaiProduct iClickLoaiProduct){
        this.mList = list;
        this.iClickLoaiProduct = iClickLoaiProduct;
        notifyDataSetChanged();
    }
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dong_loai_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        LoaiProduct loaiProduct = mList.get(position);
        if (loaiProduct == null){
            return;
        }

        holder.tvLoaiProduct.setText(loaiProduct.getTenloai());
        Picasso.get().load(loaiProduct.getHinhanh()).into(holder.imgLoaiProduct);

        // Click itemLoaiProdcut
        holder.itemLoaiProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickLoaiProduct.onClickItemLoaiProduct(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mList != null){
            return mList.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvLoaiProduct;
        private ImageView imgLoaiProduct;
        private LinearLayout itemLoaiProduct;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvLoaiProduct = itemView.findViewById(R.id.tv_loai_product);
            imgLoaiProduct = itemView.findViewById(R.id.img_loai_product);
            itemLoaiProduct = itemView.findViewById(R.id.item_loai_product);

        }
    }
}
