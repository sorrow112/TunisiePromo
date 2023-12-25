package com.example.tunisiepromo_miniproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddPromoActivity extends AppCompatActivity implements OnDateTimeSetListener {

    private EditText productNameEditText;
    private EditText discountEditText;
    private EditText productPriceEditText;
    private ImageView productImageView;
    private Button pickTime;
    private TextView dateTimeTextView;
    private Date endDate;

    private Uri selectedImageUri;
    String category = "other";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promo);
        pickTime = findViewById(R.id.pickTime);
        productNameEditText = findViewById(R.id.nomEditText);
        discountEditText = findViewById(R.id.discountEditText);
        productPriceEditText = findViewById(R.id.priceEditText);
        productImageView = findViewById(R.id.imageViewProduct);
        dateTimeTextView = findViewById(R.id.dateTime);
        endDate = new Date();
        Button selectImageButton = findViewById(R.id.buttonSelectImage);
        Log.d("categories", "fetching categories");
        fetchCategories();
        Spinner categorySpinner = findViewById(R.id.categorySpinner);

        pickTime.setOnClickListener(new AdapterView.OnClickListener(){

            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                category = (String) parentView.getItemAtPosition(position);
                Log.e("category changed to", category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                category = "autre";
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                getContent.launch(galleryIntent);
            }
        });

        Button addProductButton = findViewById(R.id.buttonAddProduct);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProductToFirebase();
            }
        });

    }
    private void uploadProductToFirebase() {
        String productName = productNameEditText.getText().toString().trim();
        String productPrice = productPriceEditText.getText().toString().trim();
        String discount = discountEditText.getText().toString().trim();


        if (!productName.isEmpty() && !productPrice.isEmpty() &&!discount.isEmpty() &&selectedImageUri != null) {
            FirebaseUser user =
                    FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("product_images/" + uid+ new Date().toString()+productName+".jpg");
            DatabaseReference profilesRef = database.getReference("profiles");
            Query query = profilesRef.orderByChild("uid").equalTo(uid);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        for (DataSnapshot profileSnapshot : dataSnapshot.getChildren()) {

                            Profile profile = profileSnapshot.getValue(Profile.class);
                            UploadTask uploadTask = storageRef.putFile(selectedImageUri);
                            uploadTask.addOnSuccessListener(taskSnapshot -> {
                                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();

                                    double price=Double.valueOf(productPrice);
                                    int disc = Integer.valueOf(discount);
                                    String pushKey = database.getReference("promos").push().getKey();
                                    //pushKey,productName,price,disc, imageUrl,0,profile.getNom(),profile.getUid()
                                    Promo product = new Promo(endDate,category,pushKey,profile.getUid(),productName,price,disc,imageUrl,profile.getNom());


                                    database.getReference("promos").child(pushKey).setValue(product);
                                    Intent intent = new Intent(AddPromoActivity.this, mainPageActivity.class);
                                    startActivity(intent);
//                                    finish();
                                });
                            }).addOnFailureListener(e -> {

                            });

                        }
                    } }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        }
    }
    private void showDateTimePicker() {

        DatePickerFragment datePickerFragment = new DatePickerFragment(this);
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }
    private final ActivityResultLauncher<Intent> getContent =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri imageUri = data.getData();
                        selectedImageUri = imageUri;
                        Picasso.get().load(imageUri).into(productImageView);
                    }
                }
            });
    private void fetchCategories() {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("categories");

        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> categoriesList = new ArrayList<>();

                    for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                        String category = categorySnapshot.getValue(String.class);
                        categoriesList.add(category);
                    }

                    Spinner categorySpinner = findViewById(R.id.categorySpinner);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddPromoActivity.this, android.R.layout.simple_spinner_item, categoriesList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);
                } else {
                    Log.e("AddPromoActivity", "No categories found in the database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AddPromoActivity", "Database error: " + error.getMessage());
            }
        });
    }

    @Override
    public void onDateTimeSet(int year, int month, int day, int hourOfDay, int minute) {

        String dateTime = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d", year, month + 1, day, hourOfDay, minute);

        dateTimeTextView.setText(dateTime);
        Log.d("DateTimeSelected", dateTime);
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        endDate = calendar.getTime();
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private OnDateTimeSetListener listener;

        public DatePickerFragment(OnDateTimeSetListener listener) {
            this.listener = listener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker.
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it.
            return new DatePickerDialog(requireContext(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            listener.onDateTimeSet(year, month, day, 23, 59);
        }
    }


}