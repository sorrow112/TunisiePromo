package com.example.tunisiepromo_miniproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, birthdateEditText,passwordInput,nomInput;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private FirebaseAuth mAuth;
    private ImageView ImageViewAvatar;
    private Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register);
        emailEditText = (TextInputEditText) findViewById(R.id.emailEditText);
        birthdateEditText = (TextInputEditText) findViewById(R.id.birthdateEditText);
        passwordInput = (TextInputEditText) findViewById(R.id.passwordEditText);
        nomInput = (TextInputEditText) findViewById(R.id.nomEditText);
        ImageViewAvatar = findViewById(R.id.imageViewAvatar);
        Button selectImageButton = findViewById(R.id.buttonSelectImage);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                getContent.launch(galleryIntent);
            }
        });
        Button submitButton = (Button) findViewById(R.id.register);

        submitButton.setOnClickListener(View ->{
                if (validateForm() &&selectedImageUri != null) {
                    // Perform registration or submit action
                    String birthdate = birthdateEditText.getText().toString();
                    String email = emailEditText.getText().toString();
                    String password = passwordInput.getText().toString();
                    String nom = nomInput.getText().toString();
                    radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    if (selectedId != -1) {
                        radioButton = findViewById(selectedId);
                        String radioButtonValue = radioButton.getText().toString();
                    } else {
                        // Handle the case where no radio button is selected
                    }
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    // L'utilisateur a été créé avec succès
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    String uid = user.getUid();
                                    //validation mail
                                    if (user != null) {
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(taskk -> {
                                                    if (taskk.isSuccessful()) {

                                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                        String pushKey = database.getReference("profiles").push().getKey();
                                                        if(pushKey!=null){
                                                            StorageReference storageRef = FirebaseStorage.getInstance().getReference("avatars/" + pushKey+ new Date().toString()+nom+".jpg");
                                                            UploadTask uploadTask = storageRef.putFile(selectedImageUri);
                                                            uploadTask.addOnSuccessListener(taskSnapshot -> {
                                                                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                                                    String imageUrl = uri.toString();
                                                                    Profile profile = new Profile(uid,birthdate,nom,radioButton.getText().toString(),imageUrl);
                                                                    // Assuming you have a "products" node in your database

                                                                    database.getReference("profiles").child(pushKey).setValue(profile);
                                                                    finish();
                                                                });
                                                            }).addOnFailureListener(e -> {
                                                                // Handle unsuccessful uploads
                                                                // ...
                                                            });
                                                        }

                                                    } else {
                                                    }
                                                });
                                    }
                                    if (user != null) {
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
        }else {
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
    private final ActivityResultLauncher<Intent> getContent =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri imageUri = data.getData();
                        selectedImageUri = imageUri;
                        Picasso.get().load(imageUri).into(ImageViewAvatar);
                    }
                }
            });


}