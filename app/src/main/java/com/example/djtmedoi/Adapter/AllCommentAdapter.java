package com.example.djtmedoi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.djtmedoi.Models.Binhluan;
import com.example.djtmedoi.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllCommentAdapter extends RecyclerView.Adapter<AllCommentAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Binhluan> mlist;

    public AllCommentAdapter(Context context, ArrayList<Binhluan> mlist) {
        this.context = context;
        this.mlist = mlist;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dong_all_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        Binhluan binhluan = mlist.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User").document(binhluan.getIduser()).collection("Profile").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot q : queryDocumentSnapshots){
                    if (q.getString("hoten") == ""){
                        holder.tvNameDongAllCmt.setText("User");
                    } else {
                        holder.tvNameDongAllCmt.setText(q.getString("hoten"));
                    }
                    if (q.getString("avatar") == ""){
                        holder.imgDongAllCmt.setImageResource(R.drawable.ic_user);
                    } else {
                        Picasso.get().load(q.getString("avatar")).into(holder.imgDongAllCmt);
                    }

                }
            }
        });
        holder.tvNoidungDongAllCmt.setText(binhluan.getNoidung());
        holder.ratingBarDongAllCmt.setRating(Float.parseFloat(binhluan.getRate()));
        holder.ratingBarDongAllCmt.setIsIndicator(true); //Không cho phép thay đổi giá trị ratingbar
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView imgDongAllCmt;
        private TextView tvNameDongAllCmt, tvNoidungDongAllCmt;
        private RatingBar ratingBarDongAllCmt;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imgDongAllCmt = itemView.findViewById(R.id.img_dong_all_cmt);
            tvNameDongAllCmt = itemView.findViewById(R.id.tv_name_dong_all_cmt);
            tvNoidungDongAllCmt = itemView.findViewById(R.id.tv_noidung_dong_all_cmt);
            ratingBarDongAllCmt = itemView.findViewById(R.id.rating_bar_dong_all_cmt);
        }
    }
}
