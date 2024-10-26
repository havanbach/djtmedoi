package com.example.djtmedoi.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.djtmedoi.fragment.bill.DaGiaoFragment;
import com.example.djtmedoi.fragment.bill.DaHuyFragment;
import com.example.djtmedoi.fragment.bill.DangGiaoFragment;
import com.example.djtmedoi.fragment.bill.DangXuLyFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new DangXuLyFragment();
            case 1:
                return new DangGiaoFragment();
            case 2:
                return new DaGiaoFragment();
            case 3:
                return new DaHuyFragment();
            default: return new DangXuLyFragment();
        }
    }

    @Override
    public int getCount() {

        // Có 4 tab nên return ra 4
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title = "Đang xử lý";
                break;
            case 1:
                title = "Đang giao";
                break;
            case 2:
                title = "Đã giao";
                break;
            case 3:
                title = "Đã hủy";
                break;
        }
        return title;
    }
}
