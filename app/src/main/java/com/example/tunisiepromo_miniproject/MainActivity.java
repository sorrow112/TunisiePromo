package com.example.tunisiepromo_miniproject;

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
        // Lorsque l'utilisateur clique sur le bouton de connexion
        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            // Ajoutez ici les contrôles de saisie nécessaires

            // Utilisez le service d'authentification Firebase pour la connexion
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // L'utilisateur est connecté avec succès
                            // Ajoutez ici le code à exécuter après la connexion réussie
                            user = mAuth.getCurrentUser();
                            if (user != null) {
                                if (user.isEmailVerified()) {
// L'e-mail de l'utilisateur a été vérifié
// Ajoutez ici le code à exécuter pour un utilisateur vérifié
                                    Toast.makeText(MainActivity.this, "Authentication success.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, mainPageActivity.class);
                                    startActivity(intent);
                                } else {
// L'utilisateur n'a pas vérifié son e-mail, affichez un message ouprenez des mesures supplémentaires
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