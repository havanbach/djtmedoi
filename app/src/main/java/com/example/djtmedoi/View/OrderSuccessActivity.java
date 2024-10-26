package com.example.djtmedoi.View;

import android.Manifest;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.djtmedoi.Models.Product;
import com.example.djtmedoi.R;

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

public class OrderSuccessActivity extends AppCompatActivity {
    private TextView tvIDHoadon, tvDateHoadon, tvSanphamHoadon,
            tvNameHoadon, tvDiachiHoadon, tvSDTHoadon, tvPhuongthucHoadon, tvGhichuHoadon, tvTongtienHoadon;
    private Button btnHoanthanhHoadon, btnXuatPDFHoadon;

    private String idhoadon, ngaydat, hoten, diachi, sdt, phuongthuc, ghichu, sanpham, tienthanhtoan;
    private Bitmap bmp,scalebmp;

    int pageWidth = 1200;
    private Date dateObj;
    private DateFormat dateFormat;

    private Product product;
    private ArrayList<Product> mlist;
    private int i = 0;
    private int j = 0;
    private String total = "";
    private int tong = 0;
    private int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        InitWidget();
        Init();
        Event();
    }

    private void Event() {

        btnHoanthanhHoadon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnXuatPDFHoadon.setOnClickListener(new View.OnClickListener() {
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
        canvas.drawText("SĐT: 098765432", 1160, 40, myPaint);
        canvas.drawText("09876543", 1160, 80, myPaint);

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        titlePaint.setTextSize(60);
        canvas.drawText("Hóa đơn", pageWidth/2, 500, titlePaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(35f);
        myPaint.setColor(Color.BLACK);
        canvas.drawText("Tên khách hàng: " + hoten, 20, 590, myPaint);
        canvas.drawText("Liên hệ: " + sdt, 20, 640, myPaint);
        canvas.drawText("Địa chỉ: " + diachi, 20, 690, myPaint);
        canvas.drawText("Phương thức: " + phuongthuc, 20, 740, myPaint);

//        if (!ghichu.equals("")){
//            myPaint.setTextAlign(Paint.Align.LEFT);
//            myPaint.setTextSize(30f);
//            myPaint.setColor(Color.BLACK);
//            canvas.drawText("Ghi chú: " + ghichu, 20, 2000, myPaint);
//        }

        myPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("ID hóa đơn: " + idhoadon, pageWidth-20, 590, myPaint);

        canvas.drawText("Ngày đặt: " + ngaydat, pageWidth-20, 640, myPaint);

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
            Number number = NumberFormat.getInstance().parse(tienthanhtoan);
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
        canvas.drawText(tienthanhtoan + " đ", pageWidth-40, 1950, myPaint);

        myPdfDocument.finishPage(myPage);
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .getAbsolutePath() + "/NVC-Invoice-" + currentDate + "-" + num + ".pdf");

        try {
            myPdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        myPdfDocument.close();
        Toast.makeText(this, "Đã xuất File. Xem trong mục lưu trữ", Toast.LENGTH_SHORT).show();
    }

    private void Init() {

        Intent intent = getIntent();
        idhoadon = intent.getStringExtra("idhoadon");
        ngaydat = intent.getStringExtra("ngaydat");
        hoten = intent.getStringExtra("hoten");
        diachi = intent.getStringExtra("diachi");
        sdt = intent.getStringExtra("sdt");
        phuongthuc = intent.getStringExtra("phuongthuc");
        ghichu = intent.getStringExtra("ghichu");
        sanpham = intent.getStringExtra("sanpham");
        tienthanhtoan = intent.getStringExtra("tienthanhtoan");

        mlist = new ArrayList<>();
        mlist = (ArrayList<Product>) intent.getSerializableExtra("serialzable");


        tvIDHoadon.setText(idhoadon);
        tvDateHoadon.setText(ngaydat);
        tvNameHoadon.setText(hoten);
        tvDiachiHoadon.setText(diachi);
        tvSDTHoadon.setText(sdt);
        tvPhuongthucHoadon.setText(phuongthuc);
        tvGhichuHoadon.setText(ghichu);
        tvSanphamHoadon.setText(sanpham);
        tvTongtienHoadon.setText(tienthanhtoan);


        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);


        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.pizzahead);
        scalebmp = Bitmap.createScaledBitmap(bmp, 1200, 518, false);

    }

    private void InitWidget() {
        tvIDHoadon = findViewById(R.id.tv_id_hoadon);
        tvDateHoadon = findViewById(R.id.tv_date_hoadon);
        tvNameHoadon = findViewById(R.id.tv_name_hoadon);
        tvDiachiHoadon = findViewById(R.id.tv_diachi_hoadon);
        tvSDTHoadon = findViewById(R.id.tv_sdt_hoadon);
        tvPhuongthucHoadon = findViewById(R.id.tv_phuongthuc_hoadon);
        tvGhichuHoadon = findViewById(R.id.tv_ghichu_hoadon);
        tvSanphamHoadon = findViewById(R.id.tv_sanpham_hoadon);
        tvTongtienHoadon = findViewById(R.id.tv_tongtien_hoadon);
        btnHoanthanhHoadon = findViewById(R.id.btn_hoanthanh_hoadon);
        btnXuatPDFHoadon = findViewById(R.id.btn_xuat_pdf_hoadon);
    }
}