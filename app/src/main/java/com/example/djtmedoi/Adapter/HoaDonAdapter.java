package com.example.djtmedoi.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.djtmedoi.Models.HoaDon;
import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.IClickCTHD;

import java.util.ArrayList;

public class HoaDonAdapter extends RecyclerView.Adapter<HoaDonAdapter.ViewHodler> {
    private ArrayList<HoaDon> arrayList;
    private  int type = 0;
    private IClickCTHD iClickCTHD;

    public void setDataHoadon(ArrayList<HoaDon> list, IClickCTHD iClickCTHD){
        this.arrayList = list;
        this.iClickCTHD = iClickCTHD;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dong_hoadon,parent,false);
        return new ViewHodler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHodler holder, int position) {

        HoaDon hoaDon = arrayList.get(position);
        holder.tvHoten.setText("Người nhận: " + hoaDon.getHoten());
        holder.tvDiachi.setText("Địa chỉ: " + hoaDon.getDiachi());
        holder.tvSdt.setText("SĐT: " + hoaDon.getSdt());
        holder.tvTongtien.setText("Tổng tiền: " + hoaDon.getTongtien() + " VNĐ");
        holder.tvNgaydat.setText(hoaDon.getNgaydat());

        holder.layoutHoadon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickCTHD.onClickCTHD(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHodler extends RecyclerView.ViewHolder{
        TextView tvHoten,tvSdt,tvDiachi,tvNgaydat,tvTongtien, tvTrangthai;
        LinearLayout layoutHoadon;

        public ViewHodler(@NonNull View itemView) {
            super(itemView);
            tvHoten = itemView.findViewById(R.id.tv_hoten);
            tvSdt = itemView.findViewById(R.id.tv_sdt);
            tvDiachi = itemView.findViewById(R.id.tv_diachi);
            tvNgaydat = itemView.findViewById(R.id.tv_ngaydat);
            tvTongtien = itemView.findViewById(R.id.tv_tongtien);
            layoutHoadon = itemView.findViewById(R.id.layout_hoadon);
        }
    }
}
