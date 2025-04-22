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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.InputStream;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
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

        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        Map<String, Object> updates = new HashMap<>();
        updates.put("nama", name);
        updates.put("email", email);

        userRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    updateSharedPreferences(name, email, currentProfileImageUrl);
                    if (imageUri == null) {
                        dismissProgress();
                        Toast.makeText(getActivity(), "Profil diperbarui", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    }
                })
                .addOnFailureListener(e -> {
                    dismissProgress();
                    Toast.makeText(getActivity(), "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating Firestore", e);
                });


        if (!TextUtils.isEmpty(newPassword)) {
            if (!TextUtils.isEmpty(oldPassword)) {
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
                user.reauthenticate(credential).addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        user.updatePassword(newPassword).addOnCompleteListener(passwordTask -> {
                            if (!passwordTask.isSuccessful()) {
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
                return;
            }
        }


        if (imageUri != null) {
            uploadImageToImgur(imageUri);
        } else {
            updateSharedPreferences(name, email, currentProfileImageUrl);
            dismissProgress();
            Toast.makeText(getActivity(), "Profil diperbarui", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        }
    }

    private void uploadImageToImgur(Uri uri) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();

            String encodedImage = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);

            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", encodedImage)
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.imgur.com/3/image")
                    .addHeader("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Upload ke Imgur gagal: " + e.getMessage());
                    runOnUi(() -> {
                        dismissProgress();
                        Toast.makeText(getActivity(), "Gagal mengunggah gambar ke Imgur", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        runOnUi(() -> {
                            dismissProgress();
                            Toast.makeText(getActivity(), "Upload gambar gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }

                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        String imageUrl = json.getJSONObject("data").getString("link");

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

                            userRef.update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        updateSharedPreferences(name, email, imageUrl);
                                        runOnUi(() -> {
                                            dismissProgress();
                                            Toast.makeText(getActivity(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                                            getActivity().onBackPressed();
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Gagal memperbarui Firestore: ", e);
                                    });

                        }

                    } catch (JSONException e) {
                        runOnUi(() -> {
                            dismissProgress();
                            Toast.makeText(getActivity(), "Gagal membaca respon server", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            dismissProgress();
            Toast.makeText(getActivity(), "Terjadi kesalahan saat membaca gambar", Toast.LENGTH_SHORT).show();
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
