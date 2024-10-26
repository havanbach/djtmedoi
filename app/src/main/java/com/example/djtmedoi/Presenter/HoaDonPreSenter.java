package com.example.djtmedoi.Presenter;

import com.example.djtmedoi.Models.HoaDon;
import com.example.djtmedoi.my_interface.HoaDonView;
import com.example.djtmedoi.my_interface.IHoaDon;

public class HoaDonPreSenter implements IHoaDon {

    private HoaDon hoaDonModels;
    private HoaDonView callback;

    public HoaDonPreSenter(HoaDonView callback) {
        this.callback = callback;
        hoaDonModels = new HoaDon(this);
    }
    public  void HandleReadDataHD(){
        hoaDonModels.HandleReadData();
    }
    public void HandleReadDataHDStatus(int i){
        hoaDonModels.HandleReadDataStatus(i);
    }

    @Override
    public void getDataHD(String id, String uid, String ghichu, String diachi, String hoten, String ngaydat, String phuongthuc, String sdt, String tongtien,Long type) {
        callback.getDataHD(id,uid,ghichu,diachi,hoten,ngaydat,phuongthuc,sdt,tongtien,type);
    }

    public void HandleGetDataHDDaGiao(){
        hoaDonModels.HandleGetHoaDonDaGiao();
    }

    @Override
    public void OnSucess() {
        callback.OnSucess();

    }

    @Override
    public void OnFail() {
      callback.OnFail();
    }

    public void CapNhatTrangThai(int i,String id) {
        hoaDonModels.HandleUpdateStatusBill(i,id);
    }

    public void HandleReadDataHD(String iduser, int positionStatus) {
        hoaDonModels.HandleReadData(iduser, positionStatus);
    }
}
