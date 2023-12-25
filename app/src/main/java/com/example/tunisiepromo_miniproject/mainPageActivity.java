package com.example.tunisiepromo_miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class mainPageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PromosAdapter productAdapter;
    private List<Promo> productList;
    private Button addprodBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        addprodBtn = (Button)findViewById(R.id.btnAddProduct);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true));

        addprodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainPageActivity.this, AddPromoActivity.class);
                startActivity(intent);
            }
        });
        productList = new ArrayList<>();
        productAdapter = new PromosAdapter(productList, this);
        recyclerView.setAdapter(productAdapter);


// Fetch product data from Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/promos");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Promo product = dataSnapshot.getValue(Promo.class);
                    productList.add(product);
                }

                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

// ...

// Fetch image URLs from Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("product_images");
        for (Promo product : productList) {
            storageReference.child(product.getImageUrl()).getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        product.setImageUrl(uri.toString());
                        productAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        // Handle error
                    });
        }
    }
}