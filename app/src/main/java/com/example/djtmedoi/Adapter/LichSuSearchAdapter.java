package com.example.djtmedoi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.IClickCTHD;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LichSuSearchAdapter extends RecyclerView.Adapter<LichSuSearchAdapter.ViewHOlder>{

    private Context context;
    private ArrayList<String> mlist;
    private IClickCTHD iClickCTHD;

    public void setdata(Context context, ArrayList<String> mlist, IClickCTHD iClickCTHD) {
        this.context = context;
        this.mlist = mlist;
        this.iClickCTHD = iClickCTHD;
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public ViewHOlder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dong_lichsu_search, parent, false);
        return new ViewHOlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHOlder holder, int position) {
        String s = mlist.get(position);
        holder.tvLichSuSearch.setText(s);
        holder.relativeItemStory.setOnClickListener(new View.OnClickListener() {
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

    public class ViewHOlder extends RecyclerView.ViewHolder{

        private TextView tvLichSuSearch;
        private RelativeLayout relativeItemStory;
        public ViewHOlder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvLichSuSearch = itemView.findViewById(R.id.tv_lichsu_search);
            relativeItemStory = itemView.findViewById(R.id.relative_item_story);
        }
    }
}
