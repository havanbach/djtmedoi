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
import com.example.djtmedoi.my_interface.IClickCTHD;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Product> mlist;
    private IClickCTHD iClickCTHD;

    public FavoriteAdapter(Context context, ArrayList<Product> mlist, IClickCTHD iClickCTHD) {
        this.context = context;
        this.mlist = mlist;
        this.iClickCTHD = iClickCTHD;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dong_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        Product product = mlist.get(position);
        holder.tvTenFavorite.setText(product.getTensp());
        holder.tvGiaFavorite.setText(NumberFormat.getInstance().format(product.getGiatien()));
        holder.tvMotaFavorite.setText(product.getMota());
        Picasso.get().load(product.getHinhanh()).into(holder.imgFavorite);

        holder.linearLayoutFavorite.setOnClickListener(new View.OnClickListener() {
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

        private LinearLayout linearLayoutFavorite;
        private ImageView imgFavorite;
        private TextView tvTenFavorite, tvGiaFavorite, tvMotaFavorite;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imgFavorite = itemView.findViewById(R.id.img_favorite);
            tvTenFavorite = itemView.findViewById(R.id.tv_ten_favorite);
            tvGiaFavorite = itemView.findViewById(R.id.tv_gia_favorite);
            tvMotaFavorite = itemView.findViewById(R.id.tv_mota_favorite);
            linearLayoutFavorite = itemView.findViewById(R.id.layout_favorite);
        }
    }
}
