package com.example.universalyoga.fragments;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.universalyoga.R;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.example.universalyoga.utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    private UserModel user;
    private ImageView ivAvatar;
    private TextView tvUserName, tvEmail, tvPhone, tvRole;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private UserDAO userDAO;
    private Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDAO = new UserDAO(getContext());

        if (getArguments() != null) {
            user = (UserModel) getArguments().getSerializable("user");
        }

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                ivAvatar.setImageBitmap(photo);
                imageUri = saveBitmapToFile(photo);
                uploadImage(imageUri);
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                ivAvatar.setImageURI(uri);
                imageUri = uri;
                uploadImage(imageUri);
            }
        });

        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(getActivity(), "Camera permission is required", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ivAvatar = view.findViewById(R.id.user_avatar);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvRole = view.findViewById(R.id.tv_role);

        tvUserName.setText(user.getName());
        tvEmail.setText(user.getEmail());
        tvPhone.setText(user.getPhoneNumber());
        tvRole.setText("Role: " + Util.capitalizeFirstLetter(user.getRole()));

        Picasso.get()
                .load(user.getProfileImage())
                .placeholder(R.drawable.ic_default_profile_image)
                .into(ivAvatar);

        ivAvatar.setOnLongClickListener(v -> {
            showImagePickerDialog();
            return true;
        });

        return view;
    }

    private void showImagePickerDialog() {
        String[] options = {"Camera", "Choose from Gallery"};
        new android.app.AlertDialog.Builder(getActivity())
                .setTitle("Select Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                    } else {
                        openGallery();
                    }
                })
                .show();
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
            String id = user.getUid();

            Util.storeFile(path, id, imageUri, new Util.OnCompleteListener<Uri>() {
                @Override
                public void onComplete(Uri downloadUri) {
                    user.setProfileImage(downloadUri.toString());
                    userDAO.updateUser(user);
                    Picasso.get()
                            .load(downloadUri)
                            .placeholder(R.drawable.ic_default_profile_image)
                            .into(ivAvatar);

                    Toast.makeText(getActivity(), "Profile image updated!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(getActivity(), "Error uploading image: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Uri saveBitmapToFile(Bitmap bitmap) {
        File file = new File(getActivity().getCacheDir(), "profile_image.jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(file);
    }
}
