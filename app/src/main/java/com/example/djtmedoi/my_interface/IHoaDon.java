package com.example.djtmedoi.my_interface;

public interface IHoaDon {
    void getDataHD(String id, String uid, String ghichu, String diachi, String hoten, String ngaydat, String phuongthuc, String sdt, String tongtien,Long type);

    void OnSucess();

    void OnFail();
}
