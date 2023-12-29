package com.example.tunisiepromo_miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MerchantDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PromosAdapter productAdapter;
    private List<Promo> productList;
    private Button addprodBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_dashboard);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FirebaseUser user =
                        FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                DatabaseReference profilesRef = FirebaseDatabase.getInstance().getReference("profiles");
                Query query = profilesRef.orderByChild("uid").equalTo(uid);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot profileSnapshot : dataSnapshot.getChildren()) {
                                // Retrieve the profile data
                                Profile profile = profileSnapshot.getValue(Profile.class);
                                if(menuItem.getItemId()==R.id.homeNav){
                                    if(profile.getRole().equals("vendeur")){
                                        startActivity(new Intent(MerchantDashboardActivity.this, MerchantDashboardActivity.class));
                                    }else{
                                        startActivity(new Intent(MerchantDashboardActivity.this, mainPageActivity.class));
                                    }
                                } else if (menuItem.getItemId()==R.id.searchNav) {
                                    startActivity(new Intent(MerchantDashboardActivity.this, RechercheActivity.class));
                                }else{
                                    if(profile.getRole().equals("vendeur")){
                                        startActivity(new Intent(MerchantDashboardActivity.this, MerchantProfileActivity.class));
                                    }else{
                                        startActivity(new Intent(MerchantDashboardActivity.this, ClientProfileActivity.class));
                                    }
                                }
                            }}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });

                return true;
            }
        });
        addprodBtn = (Button)findViewById(R.id.btnAddProduct);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true));

        addprodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MerchantDashboardActivity.this, AddPromoActivity.class);
                startActivity(intent);
            }
        });
        productList = new ArrayList<>();
        productAdapter = new PromosAdapter(productList, this);
        recyclerView.setAdapter(productAdapter);


// Fetch product data from Firebase Realtime Database
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/promos");
        Query userPromosQuery = databaseReference.orderByChild("uid").equalTo(userUid);
        userPromosQuery.addValueEventListener(new ValueEventListener() {
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