package com.example.fetchdata;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAdp extends RecyclerView.Adapter<UserAdp.Holder> {

    private Context context;
    private List<User> list;

    private EditText name, phone;

    public UserAdp(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_design, parent, false);
        Holder holder = new Holder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        User user = list.get(position);

        holder.name.setText(user.getName());
        holder.phone.setText(user.getPhone());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("person").child(user.getId()).removeValue();
                return false;
            }
        });

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);

                Button cancelBtn = dialog.findViewById(R.id.cancelBtn);
                Button okBtn = dialog.findViewById(R.id.okBtn);

                name = dialog.findViewById(R.id.name);
                name.setText(user.getName());

                phone = dialog.findViewById(R.id.phone);
                phone.setText(user.getPhone());

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isValid()) {

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("name", name.getText().toString().trim());
                            map.put("phone", phone.getText().toString().trim());

                            FirebaseDatabase.getInstance().getReference().child("person").child(user.getId()).updateChildren(map);
                            dialog.dismiss();
                        }
                    }
                });

            }
        });
    }

    private boolean isValid() {

        boolean valid = true;

        if (name.getText().length() < 3) {
            name.setError("Enter valid name");
            valid = false;
        }
        if (phone.getText().length() < 11) {
            phone.setError("Enter valid phone no");
            valid = false;
        }

        return valid;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView name, phone;
        ImageView editBtn;

        public Holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.nameData);
            phone = itemView.findViewById(R.id.phoneData);
            editBtn = itemView.findViewById(R.id.editBtn);
        }
    }
}
