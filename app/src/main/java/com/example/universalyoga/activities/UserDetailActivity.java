package com.example.universalyoga.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.universalyoga.R;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.example.universalyoga.utils.Util;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class UserDetailActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private EditText nameEditText, phoneNumberEditText;
    private EditText emailEditText, roleEditText;
    private Button btnUpdate;
    private UserDAO userDAO;
    private UserModel userModel;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        initializeViews();
        loadUserData();
        initializeToolbar();
        setupListeners();
        setupMedia();
    }

    private void initializeViews() {
        profileImageView = findViewById(R.id.user_profile_image);
        nameEditText = findViewById(R.id.user_name);
        emailEditText = findViewById(R.id.user_email);
        phoneNumberEditText = findViewById(R.id.user_phone_number);
        roleEditText = findViewById(R.id.user_role);
        btnUpdate = findViewById(R.id.btn_update_user);
        userDAO = new UserDAO(this);
    }

    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(userModel.getName() + "'s Profile");
        }
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnUpdate.setOnClickListener(v -> updateUserData());
        profileImageView.setOnClickListener(v -> showImagePickerDialog());
    }

    private void showImagePickerDialog() {
        String[] options = {"Camera", "Choose from Gallery"};
        new android.app.AlertDialog.Builder(this)
                .setTitle("Select Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                    } else {
                        openGallery();
                    }
                }).show();
    }

    private void updateUserData() {
        String newName = nameEditText.getText().toString().trim();
        String newPhoneNumber = phoneNumberEditText.getText().toString().trim();

        if (TextUtils.isEmpty(newName)) {
            nameEditText.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(newPhoneNumber)) {
            phoneNumberEditText.setError("Phone number is required");
            return;
        }

        if (!newPhoneNumber.matches("^0\\d{9}$")) {
            phoneNumberEditText.setError("Phone number must be in the format 0xxxxxxxxx");
            return;
        }

        if (imageUri != null) {
            if(Util.checkNetworkConnection(this)) {
                uploadImage(imageUri);
            } else {
                Toast.makeText(this, "No internet connection, try again later...", Toast.LENGTH_SHORT).show();
            }
        }

        userModel.setName(newName);
        userModel.setPhoneNumber(newPhoneNumber);
        userDAO.updateUser(userModel);
        Toast.makeText(UserDetailActivity.this, "User updated successfully!", Toast.LENGTH_SHORT).show();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("UPDATED_USER", userModel);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void loadUserData() {
        String uid = getIntent().getStringExtra("uid");
        userModel = userDAO.getUserByUid(uid);
        if (userModel != null) {
            nameEditText.setText(userModel.getName());
            emailEditText.setText(userModel.getEmail());
            phoneNumberEditText.setText(userModel.getPhoneNumber());
            roleEditText.setText(Util.capitalizeFirstLetter(userModel.getRole()));
            emailEditText.setEnabled(false);
            roleEditText.setEnabled(false);
            if (userModel.getProfileImage() != null && !userModel.getProfileImage().isEmpty()) {
                Picasso.get()
                        .load(userModel.getProfileImage())
                        .placeholder(R.drawable.ic_default_profile_image)
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.ic_default_profile_image);
            }
        }
    }

    private void setupMedia() {
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Bitmap photo = (Bitmap) Objects.requireNonNull(result.getData().getExtras()).get("data");
                profileImageView.setImageBitmap(photo);
                assert photo != null;
                imageUri = saveBitmapToFile(photo);
            }
        });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                profileImageView.setImageURI(uri);
                imageUri = uri;
            }
        });
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Uri saveBitmapToFile(Bitmap bitmap) {
        File file = new File(this.getCacheDir(), "profile_image.jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(file);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }

    private void openGallery() {
        galleryLauncher.launch("image/*");
    }

    private void uploadImage(Uri imageUri) {
        if (imageUri != null) {
            String path = "users/profile_images/";
            String id = userModel.getUid();
            Util.storeFile(path, id, imageUri, new Util.OnCompleteListener<Uri>() {
                @Override
                public void onComplete(Uri downloadUri) {
                    userModel.setProfileImage(downloadUri.toString());
                    userDAO.updateUser(userModel);
                    Picasso.get()
                            .load(downloadUri)
                            .placeholder(R.drawable.ic_default_profile_image)
                            .into(profileImageView);
                }
                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(UserDetailActivity.this, "Error uploading image: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

