package com.example.djtmedoi.View.Admin;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.djtmedoi.Models.HoaDon;
import com.example.djtmedoi.Presenter.HoaDonPreSenter;
import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.HoaDonView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdminChartBillActivity extends AppCompatActivity implements HoaDonView {

    private Spinner spinnerDoanhthu;
    private ArrayList<String> mlistYear;
    private ArrayList<HoaDon> mlist;
    private HoaDonPreSenter hoaDonPreSenter;

    private Button btnTinhDoanhThu, btnXuatExcel;
    private EditText edtFromDate, edtToDate;
    private TextView tvDoanhThu;
    private Toolbar toolbar;
    private BarChart barChart;
    private PieChart pieChart;
    private  float dangxuly = 0,danggiaohang=0,giaohangthanhcong=0,huyhang=0;
    int total;
    private Number number;
    int chonngaytinhtien;
    private File filePath;
    private int i = 0;
    private HSSFRow row;
    private String day = "";
    private ArrayList<BarEntry> barEntries;
    private Number numBarChart;
    private int chonBarChart;
    private Date t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chart_bill);

        InitWidget();
        Init();
        Canculator();
        Event();



    }

    private void Canculator() {

        db.collection("HoaDon").whereEqualTo("trangthai", 3)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot q : queryDocumentSnapshots){
                    String s = q.getString("tongtien");
                    try {
                        number = NumberFormat.getInstance().parse(s);
                         total += Integer.parseInt(String.valueOf(number));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                tvDoanhThu.setText(NumberFormat.getInstance().format(total));

            }
        });

    }

    private void Event() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edtFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int ngay = calendar.get(Calendar.DATE);
                int thang = calendar.get(Calendar.MONTH);
                int nam = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AdminChartBillActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        // i:năm - i1:tháng - i2:ngày
                        calendar.set(i, i1, i2);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        edtFromDate.setText(simpleDateFormat.format(calendar.getTime()));

                    }
                }, nam, thang, ngay);
                datePickerDialog.show();
            }
        });

        edtToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int ngay = calendar.get(Calendar.DATE);
                int thang = calendar.get(Calendar.MONTH);
                int nam = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AdminChartBillActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        // i:năm - i1:tháng - i2:ngày
                        calendar.set(i, i1, i2);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        edtToDate.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                }, nam, thang, ngay);
                datePickerDialog.show();
            }
        });

        btnTinhDoanhThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chonngaytinhtien=0;
                String startDate = edtFromDate.getText().toString();
                String endDate = edtToDate.getText().toString();
                if (startDate.length()==0 && endDate.length() == 0){
                    Toast.makeText(AdminChartBillActivity.this, "Vui lòng chọn ngày bắt đầu/kết thúc", Toast.LENGTH_SHORT).show();
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date ngaybatdau = sdf.parse(startDate);
                        Date ngayketthuc = sdf.parse(endDate);
                        if (ngaybatdau.compareTo(ngayketthuc) > 0){
                            Toast.makeText(AdminChartBillActivity.this, "Vui lòng chọn ngày kết thúc sau ngày bắt đầu", Toast.LENGTH_SHORT).show();
                        } else {
                            db.collection("HoaDon").whereEqualTo("trangthai", 3)
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot q : queryDocumentSnapshots){
                                        String s = q.getString("ngaydat");
                                        String tien = q.getString("tongtien");
                                        try {
                                            Date ngaygiua = sdf.parse(s);
                                            if (ngaygiua.compareTo(ngaybatdau) > 0 && ngaygiua.compareTo(ngayketthuc) < 0){
                                                Log.d("sosanhngay", "Bắt đầu < Giữa < Kết thúc");
                                                number = NumberFormat.getInstance().parse(tien);
                                                chonngaytinhtien += Integer.parseInt(String.valueOf(number));
                                                Log.d("why", "UID: " + q.getString("UID"));
                                            } else if (ngaygiua.compareTo(ngaybatdau) == 0 && ngaygiua.compareTo(ngayketthuc) < 0){
                                                Log.d("sosanhngay", "Bắt đầu = Giữa < Kết thúc");
                                                number = NumberFormat.getInstance().parse(tien);
                                                chonngaytinhtien += Integer.parseInt(String.valueOf(number));
                                                Log.d("why", "UID: " + q.getString("UID"));
                                            } else if (ngaygiua.compareTo(ngaybatdau) > 0 && ngaygiua.compareTo(ngayketthuc) == 0){
                                                Log.d("sosanhngay", "Bắt đầu < Giữa = Kết thúc");
                                                number = NumberFormat.getInstance().parse(tien);
                                                chonngaytinhtien += Integer.parseInt(String.valueOf(number));
                                                Log.d("why", "UID: " + q.getString("UID"));
                                            } else if (ngaygiua.compareTo(ngaybatdau) == 0 && ngaygiua.compareTo(ngayketthuc) == 0){
                                                Log.d("sosanhngay", "Bắt đầu = Giữa = Kết thúc");
                                                number = NumberFormat.getInstance().parse(tien);
                                                chonngaytinhtien += Integer.parseInt(String.valueOf(number));
                                                Log.d("why", "UID: " + q.getString("UID"));
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    tvDoanhThu.setText(NumberFormat.getInstance().format(chonngaytinhtien));
                                    btnXuatExcel.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


            }
        });


        btnXuatExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(AdminChartBillActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                // Tạo tên file theo ngày tháng, đường dẫn chứa file trong documents
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
//                String currentDateandTime = sdf.format(new Date());
                filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/HoaDon" + "-" + currentDate + "-" + i + ".xls");
                i++;
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
                HSSFSheet hssfSheet = hssfWorkbook.createSheet("Doanh thu"); // Tên sheet

                // Dòng đầu tiên của excel
                HSSFRow rowhead = hssfSheet.createRow((short)0);               // Hàng 1
                rowhead.createCell(0).setCellValue("STT");              // Cột 0
                rowhead.createCell(1).setCellValue("UID");              // Cột 1
                rowhead.createCell(2).setCellValue("Họ tên");           // Cột 2
                rowhead.createCell(3).setCellValue("Địa chỉ");          // Cột 3
                rowhead.createCell(4).setCellValue("SĐT");              // Cột 4
                rowhead.createCell(5).setCellValue("Ngày Đặt");         // Cột 5
                rowhead.createCell(6).setCellValue("Tổng tiền");        // Cột 6
                rowhead.createCell(7).setCellValue("ID Hóa đơn");       // Cột 7


                String startDate = edtFromDate.getText().toString();
                String endDate = edtToDate.getText().toString();
                if (startDate.length()==0 && endDate.length() == 0){
                    Toast.makeText(AdminChartBillActivity.this, "Vui lòng chọn ngày bắt đầu/kết thúc", Toast.LENGTH_SHORT).show();
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date ngaybatdau = sdf.parse(startDate);
                        Date ngayketthuc = sdf.parse(endDate);
                        if (ngaybatdau.compareTo(ngayketthuc) > 0){
                            Toast.makeText(AdminChartBillActivity.this, "Vui lòng chọn ngày kết thúc sau ngày bắt đầu", Toast.LENGTH_SHORT).show();
                        } else {
                            for (int j=0; j < mlist.size(); j++){
                                HoaDon hoaDon = mlist.get(j);
                                String s = hoaDon.getNgaydat();
                                Date ngaygiua = sdf.parse(s);
                                if (ngaygiua.compareTo(ngaybatdau) > 0 && ngaygiua.compareTo(ngayketthuc) < 0){
                                    row = hssfSheet.createRow((short)j+1);     // Hàng 2++
                                    row.createCell(0).setCellValue(j);
                                    row.createCell(1).setCellValue(hoaDon.getUid());
                                    row.createCell(2).setCellValue(hoaDon.getHoten());
                                    row.createCell(3).setCellValue(hoaDon.getDiachi());
                                    row.createCell(4).setCellValue(hoaDon.getSdt());
                                    row.createCell(5).setCellValue(hoaDon.getNgaydat());
                                    row.createCell(6).setCellValue(hoaDon.getTongtien());
                                    row.createCell(7).setCellValue(hoaDon.getId());

                                } else if (ngaygiua.compareTo(ngaybatdau) == 0 && ngaygiua.compareTo(ngayketthuc) < 0){
                                    row = hssfSheet.createRow((short)j+1);     // Hàng 2++
                                    row.createCell(0).setCellValue(j);
                                    row.createCell(1).setCellValue(hoaDon.getUid());
                                    row.createCell(2).setCellValue(hoaDon.getHoten());
                                    row.createCell(3).setCellValue(hoaDon.getDiachi());
                                    row.createCell(4).setCellValue(hoaDon.getSdt());
                                    row.createCell(5).setCellValue(hoaDon.getNgaydat());
                                    row.createCell(6).setCellValue(hoaDon.getTongtien());
                                    row.createCell(7).setCellValue(hoaDon.getId());

                                } else if (ngaygiua.compareTo(ngaybatdau) > 0 && ngaygiua.compareTo(ngayketthuc) == 0){
                                    row = hssfSheet.createRow((short)j+1);     // Hàng 2++
                                    row.createCell(0).setCellValue(j);
                                    row.createCell(1).setCellValue(hoaDon.getUid());
                                    row.createCell(2).setCellValue(hoaDon.getHoten());
                                    row.createCell(3).setCellValue(hoaDon.getDiachi());
                                    row.createCell(4).setCellValue(hoaDon.getSdt());
                                    row.createCell(5).setCellValue(hoaDon.getNgaydat());
                                    row.createCell(6).setCellValue(hoaDon.getTongtien());
                                    row.createCell(7).setCellValue(hoaDon.getId());

                                } else if (ngaygiua.compareTo(ngaybatdau) == 0 && ngaygiua.compareTo(ngayketthuc) == 0){
                                    row = hssfSheet.createRow((short)j+1);     // Hàng 2++
                                    row.createCell(0).setCellValue(j);
                                    row.createCell(1).setCellValue(hoaDon.getUid());
                                    row.createCell(2).setCellValue(hoaDon.getHoten());
                                    row.createCell(3).setCellValue(hoaDon.getDiachi());
                                    row.createCell(4).setCellValue(hoaDon.getSdt());
                                    row.createCell(5).setCellValue(hoaDon.getNgaydat());
                                    row.createCell(6).setCellValue(hoaDon.getTongtien());
                                    row.createCell(7).setCellValue(hoaDon.getId());
                                }
                            }
                        }


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (!filePath.exists()){
                            filePath.createNewFile();
                        }
                        FileOutputStream fileOutputStream= new FileOutputStream(filePath);
                        hssfWorkbook.write(fileOutputStream);

                        if (fileOutputStream!=null){
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        }
                        Toast.makeText(AdminChartBillActivity.this, "Xuất File thành công", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("error", "Lỗi: " + e.getMessage());
                    }
                }


            }
        });

        spinnerDoanhthu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                chonBarChart = 0;
                barEntries.clear();
                String item = adapterView.getItemAtPosition(position).toString();
                BieuDoDoanhThu(item);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                BieuDoDoanhThu("2020");
            }
        });
    }

    private void BieuDoDoanhThu(String item){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String thang1 = "01/01/" + item;
        String thang2 = "01/02/" + item;
        String thang3 = "01/03/" + item;
        String thang4 = "01/04/" + item;
        String thang5 = "01/05/" + item;
        String thang6 = "01/06/" + item;
        String thang7 = "01/07/" + item;
        String thang8 = "01/08/" + item;
        String thang9 = "01/09/" + item;
        String thang10 = "01/10/" + item;
        String thang11 = "01/11/" + item;
        String thang12 = "01/12/" + item;

        try {

            t1 = sdf.parse(thang1);
            t2 = sdf.parse(thang2);
            t3 = sdf.parse(thang3);
            t4 = sdf.parse(thang4);
            t5 = sdf.parse(thang5);
            t6 = sdf.parse(thang6);
            t7 = sdf.parse(thang7);
            t8 = sdf.parse(thang8);
            t9 = sdf.parse(thang9);
            t10 = sdf.parse(thang10);
            t11 = sdf.parse(thang11);
            t12 = sdf.parse(thang12);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("item", "Năm: " + item);


        for (int k = 0; k < mlist.size(); k++){
            HoaDon hoaDon = mlist.get(k);
            day = hoaDon.getNgaydat();
            try {
                Date dayHD = sdf.parse(day);
                String tienBarChart = hoaDon.getTongtien();
                if (dayHD.compareTo(t1) >= 0 && dayHD.compareTo(t2) < 0){               // Tháng 1
                    numBarChart = NumberFormat.getInstance().parse(tienBarChart);
                    chonBarChart += Integer.parseInt(String.valueOf(numBarChart));
                    barEntries.add(new BarEntry(1,chonBarChart/1000));
                    Log.d("barchart", "Tổng tiền: " + chonBarChart);
                } else if (dayHD.compareTo(t2) >= 0 && dayHD.compareTo(t3) < 0){        // Tháng 2
                    numBarChart = NumberFormat.getInstance().parse(tienBarChart);
                    chonBarChart += Integer.parseInt(String.valueOf(numBarChart));
                    Log.d("barchart", "Tổng tiền: " + chonBarChart);
                    barEntries.add(new BarEntry(2,chonBarChart/1000));
                } else if (dayHD.compareTo(t3) >= 0 && dayHD.compareTo(t4) < 0){        // Tháng 3
                    numBarChart = NumberFormat.getInstance().parse(tienBarChart);
                    chonBarChart += Integer.parseInt(String.valueOf(numBarChart));
                    Log.d("barchart", "Tổng tiền: " + chonBarChart);
                    barEntries.add(new BarEntry(3,chonBarChart/1000));
                } else if (dayHD.compareTo(t4) >= 0 && dayHD.compareTo(t5) < 0){        // Tháng 4
                    numBarChart = NumberFormat.getInstance().parse(tienBarChart);
                    chonBarChart += Integer.parseInt(String.valueOf(numBarChart));
                    Log.d("barchart", "Tổng tiền: " + chonBarChart);
                    barEntries.add(new BarEntry(4,chonBarChart/1000));
                } else if (dayHD.compareTo(t5) >= 0 && dayHD.compareTo(t6) < 0){        // Tháng 5
                    numBarChart = NumberFormat.getInstance().parse(tienBarChart);
                    chonBarChart += Integer.parseInt(String.valueOf(numBarChart));
                    Log.d("barchart", "Tổng tiền: " + chonBarChart);
                    barEntries.add(new BarEntry(5,chonBarChart/1000));
                } else if (dayHD.compareTo(t6) >= 0 && dayHD.compareTo(t7) < 0){        // Tháng 6
                    numBarChart = NumberFormat.getInstance().parse(tienBarChart);
                    chonBarChart += Integer.parseInt(String.valueOf(numBarChart));
                    Log.d("barchart", "Tổng tiền: " + chonBarChart);
                    barEntries.add(new BarEntry(6,chonBarChart/1000));
                } else if (dayHD.compareTo(t7) >= 0 && dayHD.compareTo(t8) < 0){        // Tháng 7
                    numBarChart = NumberFormat.getInstance().parse(tienBarChart);
                    chonBarChart += Integer.parseInt(String.valueOf(numBarChart));
                    Log.d("barchart", "Tổng tiền: " + chonBarChart);
                    barEntries.add(new BarEntry(7,chonBarChart/1000));
                } else if (dayHD.compareTo(t8) >= 0 && dayHD.compareTo(t9) < 0){        // Tháng 8
                    numBarChart = NumberFormat.getInstance().parse(tienBarChart);
                    chonBarChart += Integer.parseInt(String.valueOf(numBarChart));
                    Log.d("barchart", "Tổng tiền: " + chonBarChart);
                    barEntries.add(new BarEntry(8,chonBarChart/1000));
                } else if (dayHD.compareTo(t9) >= 0 && dayHD.compareTo(t10) < 0){       // Tháng 9
                    numBarChart = NumberFormat.getInstance().parse(tienBarChart);
                    chonBarChart += Integer.parseInt(String.valueOf(numBarChart));
                    Log.d("barchart", "Tổng tiền: " + chonBarChart);
                    barEntries.add(new BarEntry(9,chonBarChart/1000));
                } else if (dayHD.compareTo(t10) >= 0 && dayHD.compareTo(t11) < 0){      // Tháng 10
                    numBarChart = NumberFormat.getInstance().parse(tienBarChart);
                    chonBarChart += Integer.parseInt(String.valueOf(numBarChart));
                    Log.d("barchart", "Tổng tiền: " + chonBarChart);
                    barEntries.add(new BarEntry(10,chonBarChart/1000));
                } else if (dayHD.compareTo(t11) >= 0 && dayHD.compareTo(t12) < 0){      // Tháng 11
                    numBarChart = NumberFormat.getInstance().parse(tienBarChart);
                    chonBarChart += Integer.parseInt(String.valueOf(numBarChart));
                    Log.d("barchart", "Tổng tiền: " + chonBarChart);
                    barEntries.add(new BarEntry(11,chonBarChart/1000));
                } else if (dayHD.compareTo(t12) >= 0){                                                                // Tháng 12
                    numBarChart = NumberFormat.getInstance().parse(tienBarChart);
                    chonBarChart += Integer.parseInt(String.valueOf(numBarChart));
                    Log.d("barchart", "Tổng tiền: " + chonBarChart);
                    barEntries.add(new BarEntry(12,chonBarChart/1000));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // Initialize bar data set
        BarDataSet barDataSet = new BarDataSet(barEntries, "Doanh thu");
        // Set colors
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        // Hide draw value
        barDataSet.setDrawValues(false);
        // Set bar data
        barChart.setData(new BarData(barDataSet));
        // Set animation
        barChart.animateY(2000);
        barChart.getDescription().setText("Biểu đồ thống kê doanh thu");
        barChart.getDescription().setTextColor(Color.WHITE);
    }

    private void Init() {
        mlistYear = new ArrayList<>();
        mlistYear.add("2024");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, mlistYear);
        spinnerDoanhthu.setAdapter(arrayAdapter);
        mlist = new ArrayList<>();
        hoaDonPreSenter = new HoaDonPreSenter(this);
        hoaDonPreSenter.HandleGetDataHDDaGiao();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Back");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        barEntries = new ArrayList<>();
        ArrayList<PieEntry> pieEntries = new ArrayList<>();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("HoaDon").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot q : queryDocumentSnapshots){
                    if(q.getLong("trangthai")==1){
                        dangxuly++;
                    }else if(q.getLong("trangthai")==2){
                        danggiaohang++;
                    }else if(q.getLong("trangthai")==3){
                        giaohangthanhcong++;
                    }else{
                        huyhang++;
                    }
                }
                pieEntries.add(new PieEntry(dangxuly,"Đang Xử Lý"));
                pieEntries.add(new PieEntry(danggiaohang,"Đang giao"));
                pieEntries.add(new PieEntry(giaohangthanhcong,"Đã giao"));
                pieEntries.add(new PieEntry(huyhang,"Đã hủy"));
                ArrayList<Integer> arrayList = new ArrayList<>();

                for(int color : ColorTemplate.MATERIAL_COLORS){
                    arrayList.add(color);
                }
                for(int color : ColorTemplate.VORDIPLOM_COLORS){
                    arrayList.add(color);
                }
                Legend l = pieChart.getLegend();

                l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                l.setOrientation(Legend.LegendOrientation.VERTICAL);
                PieDataSet pieDataSet = new PieDataSet(pieEntries,"Biểu Đồ Trạng Thái Đơn Hàng");
                pieDataSet.setColors(arrayList);
                PieData pieData = new PieData(pieDataSet);
                pieData.setDrawValues(true);
                pieData.setValueFormatter(new PercentFormatter());
                pieData.setValueTextSize(14);
                pieChart.setData(pieData);
                pieChart.invalidate();
                pieChart.setDrawEntryLabels(true);
                pieChart.setUsePercentValues(true);
                pieChart.setEntryLabelTextSize(12);
                pieChart.setEntryLabelColor(Color.WHITE);
                pieChart.animateXY(2000, 2000);
                pieChart.setCenterText("Trạng Thái Đơn Hàng");

                pieChart.getDescription().setEnabled(false);

            }
        });

    }

    private void InitWidget() {
        spinnerDoanhthu = findViewById(R.id.spinner_doanhthu);
        tvDoanhThu = findViewById(R.id.tv_doanh_thu);
        edtFromDate = findViewById(R.id.edt_bill_from_date);
        edtToDate = findViewById(R.id.edt_bill_to_date);
        btnTinhDoanhThu = findViewById(R.id.btn_tinh_doanh_thu);
        btnXuatExcel = findViewById(R.id.btn_xuat_excel);

        toolbar = findViewById(R.id.toolbar);
        pieChart = findViewById(R.id.piechart);
        barChart = findViewById(R.id.barchart);
    }

    @Override
    public void getDataHD(String id, String uid, String ghichu, String diachi, String hoten, String ngaydat, String phuongthuc, String sdt, String tongtien, Long type) {
        mlist.add(new HoaDon(id, uid, ghichu, diachi, hoten, ngaydat, phuongthuc, sdt, tongtien, type));
    }

    @Override
    public void OnFail() {

    }

    @Override
    public void OnSucess() {

    }
}