package com.example.tunisiepromo_miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class RechercheActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private PromosAdapterVertical promosAdapter;
    private List<Promo> originalPromoList;
    private RecyclerView recyclerView;
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
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        originalPromoList = new ArrayList<>();
        promosAdapter = new PromosAdapterVertical(originalPromoList, this);
        recyclerView.setAdapter(promosAdapter);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/promos");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                originalPromoList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Promo product = dataSnapshot.getValue(Promo.class);
                    Log.d("test",product.getProductId());
                    originalPromoList.add(product);
                }
//                promosAdapter.setData(originalPromoList);
                promosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

// ...

// Fetch image URLs from Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("product_images");

        for (Promo product : originalPromoList) {
            storageReference.child(product.getImageUrl()).getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        product.setImageUrl(uri.toString());

                        // Check if this is the last image to be fetched
                        if (originalPromoList.indexOf(product) == originalPromoList.size() - 1) {
                            // Notify the adapter after all URLs have been fetched
                            promosAdapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle error
                    });
        }
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        // Handle search query submission (e.g., perform search)
        performSearch(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Handle changes in the search query as the user types
        performSearch(newText);
        return true;
    }

    private void performSearch(String query) {
        List<Promo> filteredList = new ArrayList<>();

        // Iterate through the original list and add items that match the query to the filtered list
        for (Promo promo : originalPromoList) {
            if (promo.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(promo);
            }
        }

        // Update the RecyclerView with the filtered list
        promosAdapter.setData(filteredList);
    }
}