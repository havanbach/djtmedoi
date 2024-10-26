package com.example.djtmedoi.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.djtmedoi.Adapter.ViewPagerAdapter;
import com.example.djtmedoi.R;
import com.google.android.material.tabs.TabLayout;

public class BillFragment extends Fragment {
    private SwipeRefreshLayout swipeBill;
    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bill, container, false);

        InitWidget();
        swipeBill.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setAdapter(viewPagerAdapter);
                        swipeBill.setRefreshing(false);
                    }
                }, 500);

            }
        });
        return view;
    }

    private void InitWidget() {
        swipeBill = view.findViewById(R.id.swipe_bill);
        tabLayout = view.findViewById(R.id.tab_Layout);
        viewPager = view.findViewById(R.id.view_Pager);
        viewPagerAdapter = new ViewPagerAdapter(getParentFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}