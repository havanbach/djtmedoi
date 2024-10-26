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
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private Context context;
    private List<Product> animalNamesList = null;
    private ArrayList<Product> arraylist;
    private IClickCTHD iClickCTHD;

    public SearchAdapter(Context context, List<Product> animalNamesList, IClickCTHD iClickCTHD) {
        this.context = context;
        this.animalNamesList = animalNamesList;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(animalNamesList);
        this.iClickCTHD = iClickCTHD;
    }

    @NotNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dong_search_product,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Product product = animalNamesList.get(position);
        holder.tvNameSeach.setText(product.getTensp());
        holder.tvMotaSeach.setText(product.getMota());
        holder.tvGiaSearch.setText(NumberFormat.getInstance().format(product.getGiatien()));
        Picasso.get().load(product.getHinhanh()).into(holder.imgSearch);

        holder.linearItemSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickCTHD.onClickCTHD(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return animalNamesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvNameSeach, tvMotaSeach, tvGiaSearch;
        private ImageView imgSearch;
        private LinearLayout linearItemSearch;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvNameSeach = itemView.findViewById(R.id.tv_name_search);
            tvMotaSeach = itemView.findViewById(R.id.tv_mota_search);
            tvGiaSearch = itemView.findViewById(R.id.tv_gia_search);
            imgSearch = itemView.findViewById(R.id.img_seach);
            linearItemSearch = itemView.findViewById(R.id.linear_item_search);
        }
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        animalNamesList.clear();
        if (charText.length() == 0) {
            animalNamesList.addAll(arraylist);
        } else {
            for (Product wp : arraylist) {
                if (wp.getTensp().toLowerCase(Locale.getDefault()).contains(charText)) {
                    animalNamesList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
