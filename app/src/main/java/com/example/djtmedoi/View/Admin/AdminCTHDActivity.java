package com.example.djtmedoi.View.Admin;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.djtmedoi.Models.HoaDon;
import com.example.djtmedoi.Models.Product;
import com.example.djtmedoi.Presenter.GioHangPresenter;
import com.example.djtmedoi.Presenter.HoaDonPreSenter;
import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.GioHangView;
import com.example.djtmedoi.my_interface.HoaDonView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdminCTHDActivity extends AppCompatActivity implements GioHangView, HoaDonView {

    private ImageView imgAdminFinishHD;
    private TextView tvAdminMaHD, tvAdminDateHD, tvAdminNameHD, tvAdminAddressHD, tvAdminSdtHD, tvAdminTotalHD, tvAdminStatusHD;
    private RecyclerView rcvAminHD;
    private Button btnAdminUpdate;
    private HoaDon hoaDon;
    private ArrayList<Product> mlist;
    private AdminCTHDAdapter adapter;
    private GioHangPresenter gioHangPresenter;
    private HoaDonPreSenter hoaDonPreSenter;

    private Button btnExportHD;
    private int num = 0;
    int pageWidth = 1200;
    private Date dateObj;
    private DateFormat dateFormat;
    private int i = 0;
    private int j = 0;
    private String total = "";
    private int tong = 0;
    private Bitmap bmp,scalebmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_cthdactivity);

        InitWidget();
        Init();
        Event();
    }

    private void Event() {
        imgAdminFinishHD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnAdminUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DiaLogUpDate();
//                Toast.makeText(AdminCTHDActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
//                finish();
            }
        });

        btnExportHD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                num++;
                CreatePDF();
            }
        });
    }

    private void CreatePDF() {
        dateObj = new Date();

        PdfDocument myPdfDocument = new PdfDocument();
        Paint myPaint = new Paint();
        Paint titlePaint = new Paint();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();

        canvas.drawBitmap(scalebmp,0,0,myPaint);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(70);
        canvas.drawText("Gofood N.V.C", pageWidth/2, 270, titlePaint);

        myPaint.setColor(Color.rgb(0, 113, 188));
        myPaint.setTextSize(30f);
        myPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("SĐT: 0906181931", 1160, 40, myPaint);
        canvas.drawText("090681931", 1160, 80, myPaint);

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        titlePaint.setTextSize(60);
        canvas.drawText("Hóa đơn", pageWidth/2, 500, titlePaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(35f);
        myPaint.setColor(Color.BLACK);
        canvas.drawText("Tên khách hàng: " + hoaDon.getHoten(), 20, 590, myPaint);
        canvas.drawText("Liên hệ: " + hoaDon.getSdt(), 20, 640, myPaint);
        canvas.drawText("Địa chỉ: " + hoaDon.getDiachi(), 20, 690, myPaint);
        canvas.drawText("Phương thức: " + hoaDon.getPhuongthuc(), 20, 740, myPaint);

//        if (!ghichu.equals("")){
//            myPaint.setTextAlign(Paint.Align.LEFT);
//            myPaint.setTextSize(30f);
//            myPaint.setColor(Color.BLACK);
//            canvas.drawText("Ghi chú: " + ghichu, 20, 2000, myPaint);
//        }

        myPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("ID hóa đơn: " + hoaDon.getId(), pageWidth-20, 590, myPaint);

        canvas.drawText("Ngày đặt: " + hoaDon.getNgaydat(), pageWidth-20, 640, myPaint);

        dateFormat = new SimpleDateFormat("HH:mm:ss");
        canvas.drawText("Thời gian: " + dateFormat.format(dateObj), pageWidth-20, 690, myPaint);

        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        canvas.drawRect(20, 780, pageWidth-20, 860, myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);
        canvas.drawText("STT", 40, 830, myPaint);
        canvas.drawText("Mô tả", 200, 830, myPaint);
        canvas.drawText("Đơn giá", 700, 830, myPaint);
        canvas.drawText("SL.", 900, 830, myPaint);
        canvas.drawText("TT.", 1050, 830, myPaint);

        canvas.drawLine(180, 790, 180, 840, myPaint);
        canvas.drawLine(680, 790, 680, 840, myPaint);
        canvas.drawLine(880, 790, 880, 840, myPaint);
        canvas.drawLine(1030, 790, 1030, 840, myPaint);

        String s = "";
        for (Product product: mlist){
            i++;
            if (product.getTensp().length() > 20 ){
                s = product.getTensp().substring(0,20) + "...";
            } else s = product.getTensp();

            canvas.drawText(i + ". ", 40, 950+j, myPaint);
            canvas.drawText(s , 200, 950+j, myPaint);
            canvas.drawText(NumberFormat.getInstance().format(product.getGiatien()), 700, 950+j, myPaint);
            canvas.drawText(String.valueOf(product.getSoluong()), 900, 950+j, myPaint);
            int gia = Integer.parseInt(product.getGiatien()+"");
            int soluong = Integer.parseInt(product.getSoluong()+"");
            total = NumberFormat.getInstance().format(gia*soluong);
            myPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(total, pageWidth-40, 950+j, myPaint);
            myPaint.setTextAlign(Paint.Align.LEFT);
            j=j+100;         //j=j+100;
        }

        try {
            Number number = NumberFormat.getInstance().parse(hoaDon.getTongtien());
            tong = Integer.parseInt(String.valueOf(number));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Thành tiền
        canvas.drawLine(680, 1735, pageWidth-20, 1735, myPaint);
        canvas.drawText("Thành tiền", 700, 1785, myPaint);
        canvas.drawText(":", 900, 1785, myPaint);
        myPaint.setTextAlign(Paint.Align.RIGHT);
        int phi = 10000;
        canvas.drawText(NumberFormat.getInstance().format(tong-phi), pageWidth-40, 1785, myPaint);

        // Phí vận chuyển
        myPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Phí vận đơn", 700, 1835, myPaint);
        canvas.drawText(":", 900, 1835, myPaint);
        myPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.valueOf(10000), pageWidth-40, 1835, myPaint);
        myPaint.setTextAlign(Paint.Align.LEFT);

        myPaint.setColor(Color.rgb(247, 147, 30));
        canvas.drawRect(680, 1875, pageWidth-20, 1975, myPaint);

        myPaint.setColor(Color.BLACK);
        myPaint.setTextSize(50f);
        myPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Tổng:", 700, 1950, myPaint);
        myPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(hoaDon.getTongtien() + " đ", pageWidth-40, 1950, myPaint);

        myPdfDocument.finishPage(myPage);
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .getAbsolutePath() + "/Admin-Invoice-" + currentDate + "-" + num + ".pdf");

        try {
            myPdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        myPdfDocument.close();
        Toast.makeText(this, "In hóa đơn hoàn tất", Toast.LENGTH_SHORT).show();
    }

    private void DiaLogUpDate() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_update_trangthai);
        dialog.show();


        Spinner spiner = dialog.findViewById(R.id.spinerCapNhat);
        String[] s = {"Chọn Mục","Đang xử lý","Đang giao hàng","Giao hàng thành công","Hủy hàng"} ;
        ArrayAdapter arrayAdapter  = new ArrayAdapter(this, android.R.layout.simple_list_item_1,s);
        spiner.setAdapter( arrayAdapter);
        spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    hoaDonPreSenter.CapNhatTrangThai(position,hoaDon.getId());
                    adapter.notifyDataSetChanged();
                    tvAdminStatusHD.setText(s[position]);
                    Toast.makeText(AdminCTHDActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
        }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void Init() {
        mlist = new ArrayList<>();
        Intent intent = getIntent();
        hoaDon = (HoaDon) intent.getSerializableExtra("CTHD");
        tvAdminMaHD.setText(hoaDon.getId());
        tvAdminDateHD.setText(hoaDon.getNgaydat());
        tvAdminNameHD.setText(hoaDon.getHoten());
        tvAdminAddressHD.setText(hoaDon.getDiachi());
        tvAdminSdtHD.setText(hoaDon.getSdt());
        tvAdminTotalHD.setText(hoaDon.getTongtien());
        switch ((int) hoaDon.getType()){
            case  1:
                tvAdminStatusHD.setText("Đang xử lý");
                break;
            case  2:
                tvAdminStatusHD.setText("Đang giao hàng");
                break;
            case  3:
                tvAdminStatusHD.setText("Giao Hàng Thành Công");
                btnExportHD.setVisibility(View.VISIBLE);
                break;
            case  4:
                tvAdminStatusHD.setText("Hủy Đơn Hàng");
                break;
        }
        hoaDonPreSenter = new HoaDonPreSenter(this);
        gioHangPresenter = new GioHangPresenter(this);
        gioHangPresenter.HandlegetDataCTHD(hoaDon.getId(),hoaDon.getUid());

        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);


        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.pizzahead);
        scalebmp = Bitmap.createScaledBitmap(bmp, 1200, 518, false);
    }

    private void InitWidget() {
        imgAdminFinishHD = findViewById(R.id.img_admin_finish);
        tvAdminMaHD = findViewById(R.id.tv_admin_mahd);
        tvAdminDateHD = findViewById(R.id.tv_admin_datehd);
        tvAdminNameHD = findViewById(R.id.tv_admin_namehd);
        tvAdminAddressHD = findViewById(R.id.tv_admin_addresshd);
        tvAdminSdtHD = findViewById(R.id.tv_admin_sdthd);
        tvAdminTotalHD = findViewById(R.id.tv_admin_totalhd);
        tvAdminStatusHD = findViewById(R.id.tv_admin_statushd);
        rcvAminHD = findViewById(R.id.rcv_admin_hd);
        btnAdminUpdate = findViewById(R.id.btn_admin_updatehd);
        btnExportHD = findViewById(R.id.btn_admin_export_hd);
    }

    @Override
    public void OnSucess() {

    }

    @Override
    public void getDataHD(String id, String uid, String ghichu, String diachi, String hoten, String ngaydat, String phuongthuc, String sdt, String tongtien, Long type) {

    }

    @Override
    public void OnFail() {

    }

    @Override
    public void getDataSanPham(String id, String idsp, String tensp, Long giatien, String hinhanh, String loaisp, String mota, Long soluong, String size, Long type, String chatlieu) {
        mlist.add(new Product(id,idsp,tensp,giatien,hinhanh,loaisp,mota,soluong,size,type,chatlieu));
        adapter = new AdminCTHDAdapter();
        adapter.setData(mlist);
        rcvAminHD.setLayoutManager(new LinearLayoutManager(this));
        rcvAminHD.setAdapter(adapter);
    }
}