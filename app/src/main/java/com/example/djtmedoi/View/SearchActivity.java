package com.example.djtmedoi.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.djtmedoi.Adapter.LichSuSearchAdapter;
import com.example.djtmedoi.Adapter.SearchAdapter;
import com.example.djtmedoi.Models.Product;
import com.example.djtmedoi.Presenter.ProductPresenter;
import com.example.djtmedoi.Presenter.StoryPresenter;
import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.IClickCTHD;
import com.example.djtmedoi.my_interface.ProductView;
import com.example.djtmedoi.my_interface.StoryView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements ProductView, StoryView {

    private SwipeRefreshLayout swipeSearch;
    private ImageView imgMic, imgQRCode;
    private ImageView imgBackSearch;
    private SearchView searchView;
    private RecyclerView rcvSearch, rcvLichSuSearch;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ArrayList<Product> mlistsearch;
    private SearchAdapter adapter;

    private ProductPresenter productPresenter;
    private StoryPresenter storyPresenter;

    private LichSuSearchAdapter lichSuSearchAdapter;
    private ArrayList<String> mlistStory;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    // AutoComplete Text
    private ArrayList<Product> mlistAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        InitWidget();
        Event();
    }

    private void Event() {
        imgBackSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hãy thử nói gì đó");

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e){
                    Toast.makeText(SearchActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ScanOptions scanOptions = new ScanOptions();
                scanOptions.setPrompt("Đưa mã QR của bạn vào máy ảnh");
                scanOptions.setBeepEnabled(true);
                scanOptions.setOrientationLocked(true);
                scanOptions.setCaptureActivity(CaptureAct.class);
                barLaucher.launch(scanOptions);
            }
        });

        swipeSearch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mlistStory.clear();
                        storyPresenter.HandleGetStory(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        lichSuSearchAdapter.setdata(SearchActivity.this, mlistStory, new IClickCTHD() {
                            @Override
                            public void onClickCTHD(int pos) {
                            }
                        });
                        swipeSearch.setRefreshing(false);
                    }
                }, 500);

            }
        });

    }
    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result ->
    {
        // Nếu quét dc mã QR và ngược lại:
       if (result.getContents() != null){
           try {
               for (Product product: mlistsearch){
                   if(result.getContents().equals(product.getId())){
                       Intent intent = new Intent(SearchActivity.this, DetailSPActivity.class);
                       intent.putExtra("search", product);
                       startActivity(intent);
                   }
               }

           } catch (Exception e){
                e.printStackTrace();
           }

       } else {
           Toast.makeText(this, "Hủy quét mã", Toast.LENGTH_SHORT).show();
       }
    });

    private void StorySearch(String text){
        HashMap<String,String> hashMap =  new HashMap<>();
        hashMap.put("noidungtimkiem", text);
        firestore.collection("LichSuTimKiem").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Story").add(hashMap);
    }

    private void InitWidget() {

        swipeSearch = findViewById(R.id.swipe_search);
        imgQRCode = findViewById(R.id.img_qrcode);
        imgMic = findViewById(R.id.img_mic);
        imgBackSearch = findViewById(R.id.img_back_search);
        searchView = findViewById(R.id.search_view);
        rcvSearch = findViewById(R.id.rcv_search_monan);

        rcvLichSuSearch = findViewById(R.id.rcv_lichsu);

        productPresenter = new ProductPresenter(this);
        storyPresenter = new StoryPresenter(this);
        mlistsearch = new ArrayList<>();
        mlistStory = new ArrayList<>();
        mlistAuto = new ArrayList<>();

        rcvSearch.setVisibility(View.GONE);

        productPresenter.HandleGetDataProduct();
        storyPresenter.HandleGetStory(FirebaseAuth.getInstance().getCurrentUser().getUid());

    }


    @Override
    public void OnSucess() {

    }

    @Override
    public void OnFail() {

    }

    @Override
    public void getDataProduct(String id, String ten, Long gia, String hinhanh, String loaisp, String mota, Long soluong, String size, Long type, String chatlieu) {
        mlistsearch.add(new Product(id, ten, gia, hinhanh, loaisp, mota, soluong, size, type, chatlieu));
        mlistAuto.add(new Product(ten));
        adapter = new SearchAdapter(SearchActivity.this, mlistsearch, new IClickCTHD() {
            @Override
            public void onClickCTHD(int pos) {
                Product product = mlistsearch.get(pos);
                Intent intent = new Intent(SearchActivity.this, DetailSPActivity.class);
                intent.putExtra("search", product);
                startActivity(intent);
            }
        });
        rcvSearch.setLayoutManager(new LinearLayoutManager(SearchActivity.this,RecyclerView.VERTICAL,false));
        rcvSearch.setAdapter(adapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                StorySearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                rcvSearch.setVisibility(View.VISIBLE);
                adapter.filter(newText);
                return true;
            }
        });


//        AutoTextAdapter autoTextAdapter = new AutoTextAdapter(this, R.layout.custom_dong_auto_text, mlistAuto);
//        autoCompleteTextView.setAdapter(autoTextAdapter);



    }

    @Override
    public void getDataStory(String noidung) {
        mlistStory.add(noidung);
        lichSuSearchAdapter = new LichSuSearchAdapter();
        lichSuSearchAdapter.setdata(SearchActivity.this, mlistStory, new IClickCTHD() {
            @Override
            public void onClickCTHD(int pos) {
                String s = mlistStory.get(pos);
                searchView.setQuery(s, false);
            }
        });
        GridLayoutManager manager = new GridLayoutManager(SearchActivity.this, 6);
        rcvLichSuSearch.setLayoutManager(manager);
        rcvLichSuSearch.setAdapter(lichSuSearchAdapter);
    }

    // Nhận đầu vào bằng giọng nói và xử lý nó
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            // get text array from voice intent
            case REQUEST_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchView.setQuery(result.get(0), false);
                }
                break;
        }
    }


}