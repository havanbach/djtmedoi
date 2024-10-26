package com.example.djtmedoi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.IClickCTHD;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BannerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<String> arrayList;
    private IClickCTHD iClickCTHD;

    public BannerAdapter(Context context, ArrayList<String> arrayList, IClickCTHD iClickCTHD) {
        this.context = context;
        this.arrayList = arrayList;
        this.iClickCTHD = iClickCTHD;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view ==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.dong_banner,null);
        ImageView banner_image = view.findViewById(R.id.image_banner);

        banner_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickCTHD.onClickCTHD(position);
            }
        });

        Picasso.get().load(arrayList.get(position).trim()).into(banner_image);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
