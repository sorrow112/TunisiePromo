package com.example.tunisiepromo_miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ...
        // Initialize Firebase Auth
//        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Obtenez les références aux champs de texte
        TextInputEditText emailEditText = findViewById(R.id.emailEditText);
        TextInputEditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.seConnecterbtn);
        TextView createAccount = findViewById(R.id.register);
        createAccount.setClickable(true);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to start the second activity
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);

                // Start the second activity
                startActivity(intent);
            }
        });
        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            if (user != null) {
                                if (user.isEmailVerified()) {
                                    Toast.makeText(MainActivity.this, "Authentication success.",
                                            Toast.LENGTH_SHORT).show();
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
                                                    if(profile.getRole().equals("vendeur")){
                                                        Intent intent = new Intent(MainActivity.this, MerchantDashboardActivity.class);
                                                        startActivity(intent);
                                                    }else{
                                                        Intent intent = new Intent(MainActivity.this, mainPageActivity.class);
                                                        startActivity(intent);
                                                    }

                                                }}
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Handle database error
                                        }
                                    });

                                } else {
                                    Toast.makeText(MainActivity.this, "il fault valider votre email.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else {
                            // La connexion a échoué, affichez un message d'erreur
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}