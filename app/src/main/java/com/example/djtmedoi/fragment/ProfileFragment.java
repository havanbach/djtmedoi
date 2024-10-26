package com.example.djtmedoi.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.djtmedoi.MainActivity;
import com.example.djtmedoi.R;
import com.example.djtmedoi.View.SignInActivity;
import com.example.djtmedoi.ultil.NetworkUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private Uri mUri;
    private ProgressDialog progressDialog;
    private MainActivity mMainActivity;

    private View view;
    private CircleImageView imgAvatar;
    private EditText edtFullName, edtAddress, edtPhoneNumber, edtDate;
    private TextView tvEmail;
    private RadioButton rdoNam, rdoNu;
    private RadioGroup rdoGroup;
    private Button btnUpdateprofile;
    private LinearLayout layoutLogout;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private StorageReference storageReference;
    private  String key = "";

    private boolean reloadData = false;

    DatabaseReference reference;
    FirebaseUser firebaseUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        InitWidget();
        Init();
//        Log.e("ep", mainActivity.Password());

        if (NetworkUtil.isNetworkConnected(getContext())){
            reloadData = true;
            LoadInfo();
            setUserInformation();
            Event();
        } else {
            reloadData = false;
        }
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        Log.d("fmt", "onCreateView");
        reload();
        return view;
    }

    void reload(){
        if (reloadData){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentByTag("main_fragment");
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if(currentFragment != null) {
                fragmentTransaction.detach(currentFragment);
                fragmentTransaction.attach(currentFragment);
                fragmentTransaction.commit();
            }
        } else {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentByTag("main_fragment");
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if(currentFragment != null) {
                fragmentTransaction.detach(currentFragment);
                fragmentTransaction.attach(currentFragment);
                fragmentTransaction.commit();
            }
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("fmt", "onResume");
        if (!reloadData){
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            onAttach(getContext());

            reloadData = false;
        } else {
            reloadData = true;
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            onAttach(getContext());
        }
    }

//    @Override
//    public void onAttach(@NonNull @NotNull Context context) {
//        super.onAttach(context);
//        Log.d("fmt", "onAttach");
//        if (NetworkUtil.isNetworkConnected(getContext())){
//            reloadData = true;
//            LoadInfo();
//            setUserInformation();
//            Event();
//            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
//        } else {
//            reloadData = false;
//            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
//        }
//    }

    private void LoadInfo() {
        firestore.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Profile")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {

                if(queryDocumentSnapshots.size()>0){
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    if(documentSnapshot!=null){
                        key = documentSnapshot.getId();
                        try{
                            edtAddress.setText(documentSnapshot.getString("diachi").length()>0 ?
                                    documentSnapshot.getString("diachi") : "");
                            edtFullName.setText(documentSnapshot.getString("hoten").length()>0 ?
                                    documentSnapshot.getString("hoten") : "");
                            edtPhoneNumber.setText(documentSnapshot.getString("sdt").length()>0 ?
                                    documentSnapshot.getString("sdt") : "");
                            edtDate.setText(documentSnapshot.getString("ngaysinh").length()>0 ?
                                    documentSnapshot.getString("ngaysinh") : "");
                            String sex = documentSnapshot.getString("gioitinh");
                            if (sex.length()>0){
                                if (sex.equals("Nam")){
                                    rdoNam.setChecked(true);
                                } else {
                                    rdoNu.setChecked(true);
                                }
                            } else {
                                rdoGroup.clearCheck();
                            }
                            if(documentSnapshot.getString("avatar").length()>0){
                                Picasso.get().load(documentSnapshot.getString("avatar").trim()).into(imgAvatar);
                            }
                        }catch (Exception e){
                        }

                    }else{
                        HashMap<String,String> hashMap=  new HashMap<>();
                        hashMap.put("diachi","");
                        hashMap.put("hoten","");
                        hashMap.put("sdt","");
                        hashMap.put("ngaysinh","");
                        hashMap.put("gioitinh","");
                        hashMap.put("avatar","");
                        hashMap.put("iduser", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        firestore.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("Profile").add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(@NonNull DocumentReference documentReference) {
                                key = documentReference.getId();
                            }
                        });

                    }
                }else{
                    HashMap<String,String> hashMap=  new HashMap<>();
                    hashMap.put("diachi","");
                    hashMap.put("hoten","");
                    hashMap.put("sdt","");
                    hashMap.put("ngaysinh","");
                    hashMap.put("gioitinh","");
                    hashMap.put("avatar","");
                    hashMap.put("iduser", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    firestore.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .collection("Profile").add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(@NonNull DocumentReference documentReference) {
                            key = documentReference.getId();
                        }
                    });
                }
            }
        });
    }

    // Hàm set thông tin người dùng hiện tại khi click vào My Profile
    private void setUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            return;
        }
        edtFullName.setText(user.getDisplayName());
        tvEmail.setText(user.getEmail());
        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.ic_avatar_default).into(imgAvatar);
    }

    private void Init() {
        progressDialog = new ProgressDialog(getActivity());
        mMainActivity = (MainActivity) getActivity();
    }

    private void Event() {

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                            || getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                        getActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},123);
                    }else{
                        PickGallary();
                    }
                }else{
                    PickGallary();
                }
            }
        });

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int ngay = calendar.get(Calendar.DATE);
                int thang = calendar.get(Calendar.MONTH);
                int nam = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        // i:năm - i1:tháng - i2:ngày
                        calendar.set(i, i1, i2);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        edtDate.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                }, nam, thang, ngay);
                datePickerDialog.show();
            }
        });

        btnUpdateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strFullName = edtFullName.getText().toString().trim();
                String strAddress = edtAddress.getText().toString().trim();
                String strSDT = edtPhoneNumber.getText().toString().trim();
                String strDate = edtDate.getText().toString().trim();
                String strSex;
                if (rdoNam.isChecked()){
                    strSex = "Nam";
                } else {
                    strSex = "Nữ";
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null){
                    return;
                }
                progressDialog.show();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(strFullName)
                        .setPhotoUri(mUri)
                        .build();

                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            mMainActivity.setProFile();
                        }
                    }
                });

                Map<String, Object> chinh = new HashMap<>();
                chinh.put("hoten", strFullName);
                chinh.put("diachi", strAddress);
                chinh.put("sdt", strSDT);
                chinh.put("ngaysinh", strDate);
                chinh.put("gioitinh", strSex);

                firestore.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("Profile").document(key)
                        .update(chinh).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

                // Import vào Realtime của Firebase
                reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                HashMap<String, Object> map = new HashMap<>();
                map.put("iduser", FirebaseAuth.getInstance().getCurrentUser().getUid());
                map.put("name", strFullName);
                map.put("search", strFullName.toLowerCase());
                reference.updateChildren(map);
            }
        });

        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder buidler = new AlertDialog.Builder(getActivity());
                buidler.setTitle("Thông báo");
                buidler.setIcon(R.drawable.icons8_shutdown);
                buidler.setMessage("Bạn có thực sự muốn đăng xuất khỏi tài khoản này không?");
                buidler.setPositiveButton("Đăng xuất", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), SignInActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                buidler.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                buidler.show();

            }
        });

        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_profile);
                dialog.show();
                EditText edtNewEmail = dialog.findViewById(R.id.edt_new_email_dialog);
                ImageView imgCancelDialog = dialog.findViewById(R.id.img_cancel_dialog);
                Button btnCapnhatDialog = dialog.findViewById(R.id.btn_capnhat_dialog);

                edtNewEmail.setText(user.getEmail());
                imgCancelDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

                btnCapnhatDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String strNewEmail = edtNewEmail.getText().toString().trim();
                        progressDialog.show();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateEmail(strNewEmail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            dialog.cancel();
                                            Toast.makeText(getActivity(), "User Email Address Updated", Toast.LENGTH_SHORT).show();
                                            mMainActivity.setProFile();
                                        }
                                    }
                                });
                    }
                });
            }
        });

    }

    private void InitWidget() {
        imgAvatar = view.findViewById(R.id.img_avatar);
        edtFullName = view.findViewById(R.id.edt_full_name);
        edtAddress = view.findViewById(R.id.edt_address);
        edtPhoneNumber = view.findViewById(R.id.edt_phone);
        edtDate = view.findViewById(R.id.edt_date);
        rdoNam = view.findViewById(R.id.rdo_nam);
        rdoNu = view.findViewById(R.id.rdo_nu);
        rdoGroup = view.findViewById(R.id.rdo_group);
        tvEmail = view.findViewById(R.id.tv_email_profile);
        btnUpdateprofile = view.findViewById(R.id.btn_update_profile);
        layoutLogout = view.findViewById(R.id.layout_logout);
    }

    private void PickGallary() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,123);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 123 && resultCode== getActivity().RESULT_OK){
            Uri uri = data.getData();
            Log.d("CHECKED",uri+" ");
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] datas = baos.toByteArray();
                String filename = FirebaseAuth.getInstance().getCurrentUser().getUid();
                storageReference= FirebaseStorage.getInstance("gs://doan-dc57a.appspot.com/").getReference();
                storageReference.child("Profile").child(filename+".jpg").putBytes(datas).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        if(taskSnapshot.getTask().isSuccessful()){
                            storageReference.child("Profile").child(filename+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(@NonNull Uri uri) {
                                    firestore.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .collection("Profile").document(key)
                                            .update("avatar",uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                imgAvatar.setImageBitmap(bitmap);
                                            }
                                        }
                                    });

                                    // update vào realtime database của firebase
                                    reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("avatar", uri.toString());
                                    reference.updateChildren(map);
                                }
                            });
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                Log.d("CHECKED",e.getMessage());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}