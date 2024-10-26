package com.example.djtmedoi.View.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.djtmedoi.Models.User;
import com.example.djtmedoi.R;
import com.example.djtmedoi.my_interface.IClickCTHD;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminUsersAdapter extends RecyclerView.Adapter<AdminUsersAdapter.ViewHolder>{

    private Context context;
    private ArrayList<User> mlistUser;
    private IClickCTHD iClickCTHD;

    public AdminUsersAdapter(Context context, ArrayList<User> mlistUser, IClickCTHD iClickCTHD) {
        this.context = context;
        this.mlistUser = mlistUser;
        this.iClickCTHD = iClickCTHD;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dong_admin_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        User user = mlistUser.get(position);
        if (user.getName().equals("")){
            holder.tvNameUsers.setText("User");
        } else {
            holder.tvNameUsers.setText(user.getName());
        }

        holder.tvEmailUsers.setText(user.getEmail());

        if (user.getAvatar().equals("")){
            holder.cirUsers.setImageResource(R.drawable.ic_user);
        } else {
            Picasso.get().load(user.getAvatar()).into(holder.cirUsers);
        }

        holder.relativeItemUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickCTHD.onClickCTHD(position);
            }
        });

//        holder.imgDeleteUsers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder buidler = new AlertDialog.Builder(context);
//                buidler.setTitle("Thông báo");
//                buidler.setMessage("Bạn có thực sự muốn xóa tài khoản này không?");
//                buidler.setPositiveButton("Đăng xuất", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        FirebaseFirestore db = FirebaseFirestore.getInstance();
//                        db.collection("IDUser").whereEqualTo("iduser", user.getIduser()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                            @Override
//                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                for (QueryDocumentSnapshot q : queryDocumentSnapshots){
//                                    db.collection("IDUser").document(q.getId()).delete();
//                                }
//
//                            }
//                        });
//
//                        db.collection("User").document(user.getIduser()).delete();
//                        mlistUser.remove(position);
//                        notifyDataSetChanged();
//                    }
//                });
//                buidler.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                    }
//                });
//                buidler.show();
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return mlistUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView cirUsers;
        private TextView tvNameUsers, tvEmailUsers;
        private ImageView imgDeleteUsers;
        private RelativeLayout relativeItemUsers;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            cirUsers = itemView.findViewById(R.id.cir_users);
            tvNameUsers = itemView.findViewById(R.id.tv_name_users);
            tvEmailUsers = itemView.findViewById(R.id.tv_email_users);
//            imgDeleteUsers = itemView.findViewById(R.id.img_delete_users);
            relativeItemUsers = itemView.findViewById(R.id.relative_item_users);
        }
    }
}
