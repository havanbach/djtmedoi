package com.example.djtmedoi.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.djtmedoi.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class NotifyFragment extends Fragment implements OnMapReadyCallback {
    private Toolbar toolbar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();;
    private TextView txtdiachi,txtsdt,txtnoidung;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_notify, container, false);

        toolbar = v.findViewById(R.id.toolbar);
        txtdiachi = v.findViewById(R.id.txtdiachi);
        txtsdt = v.findViewById(R.id.txtsdt);
        txtnoidung = v.findViewById(R.id.txtnoidung);

        db.collection("ThongTinCuaHang").document("sBI6zx4YiA3TPhUISVxo")
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {

                txtdiachi.setText("Địa chỉ : "+documentSnapshot.getString("diachi"));
                txtsdt.setText("Liên hệ : "+documentSnapshot.getString("sdt"));
                txtnoidung.setText("Nội Dung : "+documentSnapshot.getString("noidung"));


            }
        });

        // GG maps
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d("state", "onCreateView");
        return v;
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        db.collection("ThongTinCuaHang").document("sBI6zx4YiA3TPhUISVxo").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                txtdiachi.setText("Địa chỉ : "+documentSnapshot.getString("diachi"));
                txtsdt.setText("Liên hệ : "+documentSnapshot.getString("sdt"));
                txtnoidung.setText("Nội Dung : "+documentSnapshot.getString("noidung"));
                //đọc vị trí gg map

            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("state", "onSave");
    }
}