package com.example.fetchdata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button addBtn;

    private EditText phone, name;

    private SearchView searchView;
    private DatabaseReference reference;

    private RecyclerView recyclerView;
    private UserAdp adp;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        fetchData();
    }

    private void init() // used for widget initialization
    {
        addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(this);

        reference = FirebaseDatabase.getInstance().getReference("person"); // firebase initialization

        recyclerView = findViewById(R.id.recycler); // initialize recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchView = findViewById(R.id.searchView);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }
    }

    private void search(String text) { // set searched data to the recycler view
        ArrayList<User> searchList = new ArrayList<>();
        for (User i : users) {
            if (i.getName().toLowerCase().contains(text.toLowerCase()) ||  i.getPhone().contains(text)) {
                searchList.add(i);
            }
        }
        adp = new UserAdp(this, searchList);
        recyclerView.setAdapter(adp);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBtn:
                createDialog();
                break;
        }
    }

    private void createDialog() { // this method creates dialog for add contact
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        Button cancelBtn = dialog.findViewById(R.id.cancelBtn);
        Button okBtn = dialog.findViewById(R.id.okBtn);
        name = dialog.findViewById(R.id.name);
        phone = dialog.findViewById(R.id.phone);

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
                    User user = new User(name.getText().toString().trim(), phone.getText().toString().trim());
                    addToDb(user);
                    dialog.dismiss();
                }

            }
        });
    }

    private boolean isValid() { // this method valid the data for firebase

        boolean valid = true;

        if (name.getText().length() < 3) {
            name.setError("Enter valid name");
            valid = false;
        }
        if (phone.getText().length() < 11 || phone.getText().length() > 11) {
            phone.setError("Enter valid phone no");
            valid = false;
        }
        return valid;
    }

    private void addToDb(User user) { // this method add data to firebase
        user.setId(reference.push().getKey());
        reference.child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Data added successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchData() { // this method fetches data from firebase
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                users = new ArrayList<User>();

                for (DataSnapshot i : snapshot.getChildren()) {
                    users.add(i.getValue(User.class));
                }
                setDataToRecycler(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setDataToRecycler(List<User> list) {  // this method sets data on recycler view
        adp = new UserAdp(MainActivity.this, list);
        recyclerView.setAdapter(adp);
    }
}