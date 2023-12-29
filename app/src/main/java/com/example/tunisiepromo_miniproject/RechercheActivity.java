package com.example.tunisiepromo_miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RechercheActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);
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
                                        startActivity(new Intent(RechercheActivity.this, MerchantDashboardActivity.class));
                                    }else{
                                        startActivity(new Intent(RechercheActivity.this, mainPageActivity.class));
                                    }
                                } else if (menuItem.getItemId()==R.id.searchNav) {
                                    startActivity(new Intent(RechercheActivity.this, RechercheActivity.class));
                                }else{
                                    if(profile.getRole().equals("vendeur")){
                                        startActivity(new Intent(RechercheActivity.this, MerchantProfileActivity.class));
                                    }else{
                                        startActivity(new Intent(RechercheActivity.this, ClientProfileActivity.class));
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
    }
}