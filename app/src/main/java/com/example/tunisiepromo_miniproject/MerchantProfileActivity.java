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
import android.widget.ImageView;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MerchantProfileActivity extends AppCompatActivity {

    private ImageView avatar;
    private TextView title;
    private TextView name;
    private TextView email;
    private TextView birthdate;
    private TextView location;
    private RecyclerView recyclerView;
    private PromosAdapterVertical productAdapter;
    private List<Promo> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_profile);
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
                                        startActivity(new Intent(MerchantProfileActivity.this, MerchantDashboardActivity.class));
                                    }else{
                                        startActivity(new Intent(MerchantProfileActivity.this, mainPageActivity.class));
                                    }
                                } else if (menuItem.getItemId()==R.id.searchNav) {
                                    startActivity(new Intent(MerchantProfileActivity.this, RechercheActivity.class));
                                }else{
                                    if(profile.getRole().equals("vendeur")){
                                        startActivity(new Intent(MerchantProfileActivity.this, MerchantProfileActivity.class));
                                    }else{
                                        startActivity(new Intent(MerchantProfileActivity.this, ClientProfileActivity.class));
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
        FirebaseUser user =
                FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String myEmail = user.getEmail();
        DatabaseReference profilesRef = FirebaseDatabase.getInstance().getReference("profiles");
        Query query = profilesRef.orderByChild("uid").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot profileSnapshot : dataSnapshot.getChildren()) {
                        // Retrieve the profile data
                        Profile profile = profileSnapshot.getValue(Profile.class);
                        title = (TextView)findViewById(R.id.title);
                        avatar = (ImageView) findViewById(R.id.imageView1);
                        name = (TextView)findViewById(R.id.nom);
                        email = (TextView)findViewById(R.id.email);
                        birthdate = (TextView)findViewById(R.id.birthdate);
                        location = (TextView)findViewById(R.id.location);
                        title.setText(profile.getNom());
                        location.setText(profile.getLocation());
                        name.setText(profile.getNom());
                        Picasso.get().load(profile.getAvatar()).into(avatar);
                        email.setText(myEmail);
                        birthdate.setText(profile.getBirthdate());

                    }}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true));

        productList = new ArrayList<>();
        productAdapter = new PromosAdapterVertical(productList, this);
        recyclerView.setAdapter(productAdapter);


// Fetch product data from Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/promos");
        Query userPromosQuery = databaseReference.orderByChild("uid").equalTo(uid);
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