package com.example.djtmedoi.my_interface;

public interface IGioHang {
    void OnSucess();

    void OnFail();

    void getDataSanPham(String id, String id_sp,String tensp, Long giatien, String hinhanh, String loaisp, String mota, Long soluong, String size, Long type, String chatlieu);
}