package com.example.tunisiepromo_miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, birthdateEditText,passwordInput,nomInput;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register);
        emailEditText = (TextInputEditText) findViewById(R.id.emailEditText);
        birthdateEditText = (TextInputEditText) findViewById(R.id.birthdateEditText);
        passwordInput = (TextInputEditText) findViewById(R.id.passwordEditText);
        nomInput = (TextInputEditText) findViewById(R.id.nomEditText);
        Button submitButton = (Button) findViewById(R.id.register);
        submitButton.setOnClickListener(View ->{
                if (validateForm()) {
                    // Perform registration or submit action
                    String birthdate = birthdateEditText.getText().toString();
                    String email = emailEditText.getText().toString();
                    String password = passwordInput.getText().toString();
                    String nom = nomInput.getText().toString();
                    // controle de saisie
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        // Affichez un message d'erreur, l'e-mail n'est pas valide
                    }
                    // Exemple de contrôle de saisie pour le mot de passe (minimum 6caractères)
                    if (password.length() < 6) {
                        // Affichez un message d'erreur, le mot de passe est trop court
                    }

                    Task<AuthResult> authResultTask = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    // L'utilisateur a été créé avec succès
                                    FirebaseUser user =
                                            FirebaseAuth.getInstance().getCurrentUser();
                                    String uid = user.getUid();
//                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    if (user != null) {
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(taskk -> {
                                                    if (taskk.isSuccessful()) {

                                                    } else {
// L'envoi de l'e-mail de vérification a échoué, affichez un message d'erreur
                                                    }
                                                });
                                    }
                                    if (user != null) {
                                        // Assuming you have a 'users' node in your database
                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

                                        // Save additional user information (name and birthdate) to the database
                                        userRef.child("name").setValue(nom);
                                        userRef.child("birthdate").setValue(birthdate);

                                        user.sendEmailVerification()
                                                .addOnCompleteListener(taskk -> {
                                                    if (taskk.isSuccessful()) {
                                                        // Email verification sent successfully
                                                    } else {
                                                        // Email verification sending failed
                                                    }
                                                });
                                        // Ici, vous pouvez stocker ou utiliser l'UID comme nécessaire
                                    } else {
                                        // La création du compte a échoué, affichez un message d'erreur
                                    }
                                }});
                
                }else{
                    Toast.makeText(RegisterActivity.this, "Informations invalides", Toast.LENGTH_SHORT).show();
                }
            });


    }
    private boolean validateForm() {
        boolean isValid = true;

        // Email validation
        String email = emailEditText.getText().toString().trim();
        if (!isValidEmail(email)) {
            emailEditText.setError("Invalid email format");
            isValid = false;
        } else {
            emailEditText.setError(null);
        }

        // Birthdate validation
        String birthdate = birthdateEditText.getText().toString().trim();
        if (!isValidBirthdate(birthdate)) {
            birthdateEditText.setError("Invalid birthdate format or it's greater than the current date");
            isValid = false;
        } else {
            birthdateEditText.setError(null);
        }

        return isValid;
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidBirthdate(String birthdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = sdf.parse(birthdate);
            Date currentDate = new Date();

            // Compare the birthdate with the current date
            return date != null && date.before(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}