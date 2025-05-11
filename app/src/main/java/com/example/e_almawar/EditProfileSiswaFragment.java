package com.example.e_almawar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.HashMap;
import java.util.Map;

public class EditProfileSiswaFragment extends Fragment {

    private EditText etName, etEmail, etOldPassword, etNewPassword;
    private Button btnSave, btnChooseImage;
    private ImageView ivProfile;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "EditProfileFragment";
    private static final String IMGUR_CLIENT_ID = "26b0a70ffb1b617"; // Ganti dengan Client-ID milikmu

    private FirebaseAuth mAuth;
    private String currentProfileImageUrl = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_siswa_akun, container, false);

        mAuth = FirebaseAuth.getInstance();
        initViews(view);
        loadUserData();

        btnChooseImage.setOnClickListener(v -> openFileChooser());
        btnSave.setOnClickListener(v -> updateProfile());

        return view;
    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etOldPassword = view.findViewById(R.id.etOldPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        btnSave = view.findViewById(R.id.btnSave);
        btnChooseImage = view.findViewById(R.id.btnChooseImage);
        ivProfile = view.findViewById(R.id.ivProfile);
        ivProfile.setImageResource(R.drawable.default_profile);
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(uid);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("nama");
                    String email = documentSnapshot.getString("email");
                    currentProfileImageUrl = documentSnapshot.getString("profileUrl");

                    etName.setText(name);
                    etEmail.setText(email);

                    if (!TextUtils.isEmpty(currentProfileImageUrl)) {
                        Glide.with(this)
                                .load(currentProfileImageUrl)
                                .circleCrop()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .placeholder(R.drawable.default_profile)
                                .into(ivProfile);
                    }
                }
            });
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity(), "Nama dan email tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = ProgressDialog.show(getActivity(), "", "Memperbarui profil...", true, false);
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            dismissProgress();
            Toast.makeText(getActivity(), "Pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Runnable afterPasswordUpdate = () -> {
            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(uid);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String currentName = documentSnapshot.getString("nama");
                    String currentEmail = documentSnapshot.getString("email");

                    boolean dataBerubah = !currentName.equals(name) || !currentEmail.equals(email);

                    // Jika tidak ada perubahan apa pun (nama, email, password, foto)
                    if (!dataBerubah && imageUri == null && TextUtils.isEmpty(newPassword)) {
                        dismissProgress();
                        Toast.makeText(getActivity(), "Tidak ada perubahan", Toast.LENGTH_SHORT).show();

                        // Navigasi balik ke fragment sebelumnya (SiswaAkunFragment)
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, new SiswaAkunFragment());
                        transaction.addToBackStack(null);
                        transaction.commit();
                        return;
                    }

                    if (imageUri != null) {
                        // Jika ada gambar yang diunggah
                        uploadImageToImgur(imageUri);
                    } else {
                        // Update Firestore tanpa gambar
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("nama", name);
                        updates.put("email", email);

                        userRef.update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    updateSharedPreferences(name, email, currentProfileImageUrl);
                                    dismissProgress();
                                    Toast.makeText(getActivity(), "Profil diperbarui", Toast.LENGTH_SHORT).show();

                                    // Kembali ke fragment akun
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragment_container, new SiswaAkunFragment());
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                })
                                .addOnFailureListener(e -> {
                                    dismissProgress();
                                    Toast.makeText(getActivity(), "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Error updating Firestore", e);
                                });
                    }
                }
            });
        };

        // Jika ada perubahan password
        if (!TextUtils.isEmpty(newPassword)) {
            if (!TextUtils.isEmpty(oldPassword)) {
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
                user.reauthenticate(credential).addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        user.updatePassword(newPassword).addOnCompleteListener(passwordTask -> {
                            if (passwordTask.isSuccessful()) {
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("user_password", newPassword);
                                editor.apply();
                                afterPasswordUpdate.run(); // Lanjutkan update
                            } else {
                                dismissProgress();
                                Toast.makeText(getActivity(), "Gagal memperbarui kata sandi", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        dismissProgress();
                        Toast.makeText(getActivity(), "Kata sandi lama salah. Gagal mengubah.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                dismissProgress();
                Toast.makeText(getActivity(), "Masukkan kata sandi lama untuk memperbarui", Toast.LENGTH_SHORT).show();
            }
        } else {
            afterPasswordUpdate.run(); // Tidak ada perubahan password
        }
    }


    private void uploadImageToImgur(Uri uri) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();

            // Log image size for debugging
            Log.d(TAG, "Image size: " + imageBytes.length);

            OkHttpClient client = new OkHttpClient();

            // Build the request body with the image as a multipart form
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", "profile_image.jpg", RequestBody.create(MediaType.parse("image/jpeg"), imageBytes))
                    .build();

            // Log the request body for debugging
            Log.d(TAG, "Request body created");

            // Create the request to Imgur API
            Request request = new Request.Builder()
                    .url("https://api.imgur.com/3/image")
                    .addHeader("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                    .post(requestBody)
                    .build();

            // Execute the request asynchronously
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Log the error
                    Log.e(TAG, "Upload to Imgur failed: " + e.getMessage());
                    runOnUi(() -> {
                        dismissProgress();
                        Toast.makeText(getActivity(), "Failed to upload image to Imgur", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        // Log the response failure
                        Log.e(TAG, "Upload failed: " + response.message());
                        runOnUi(() -> {
                            dismissProgress();
                            Toast.makeText(getActivity(), "Upload image failed: " + response.message(), Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }

                    // Log the successful response
                    String responseBody = response.body().string();
                    Log.d(TAG, "Imgur response: " + responseBody);

                    try {
                        // Parse the response JSON
                        JSONObject json = new JSONObject(responseBody);
                        String imageUrl = json.getJSONObject("data").getString("link");

                        // Log the image URL
                        Log.d(TAG, "Image uploaded successfully. URL: " + imageUrl);

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            String name = etName.getText().toString();
                            String email = etEmail.getText().toString();

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userRef = db.collection("users").document(userId);

                            Map<String, Object> updates = new HashMap<>();
                            updates.put("nama", name);
                            updates.put("email", email);
                            updates.put("profileUrl", imageUrl);

                            // Update the Firestore document with the new profile URL
                            userRef.update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        updateSharedPreferences(name, email, imageUrl);
                                        runOnUi(() -> {
                                            dismissProgress();
                                            Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                            getActivity().onBackPressed();
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error updating Firestore", e);
                                    });
                        }
                    } catch (JSONException e) {
                        // Handle JSON parsing error
                        Log.e(TAG, "Error parsing Imgur response", e);
                        runOnUi(() -> {
                            dismissProgress();
                            Toast.makeText(getActivity(), "Error reading server response", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            dismissProgress();
            Toast.makeText(getActivity(), "Error reading image", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSharedPreferences(String name, String email, String imageUrl) {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_name", name);
        editor.putString("user_email", email);
        editor.putString("profile_image_url", imageUrl);
        editor.apply();
    }

    private void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void runOnUi(Runnable runnable) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(runnable);
        }
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        if (cursor != null) {
            try {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(index);
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(getContext()).load(imageUri).circleCrop().into(ivProfile);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }

}
