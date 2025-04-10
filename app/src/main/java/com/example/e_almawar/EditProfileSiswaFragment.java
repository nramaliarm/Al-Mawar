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

public class EditProfileSiswaFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText etName, etEmail, etOldPassword, etNewPassword;
    private Button btnSave, btnChooseImage;
    private ImageView ivProfile;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "EditProfileSiswaFragment";
    private String currentProfileImageUrl = "";
    private ProgressDialog progressDialog;

    public EditProfileSiswaFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_siswa_akun, container, false);

        // Initialize Firebase Auth, Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views (EditText, Button, ImageView)
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etOldPassword = view.findViewById(R.id.etOldPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        btnSave = view.findViewById(R.id.btnSave);
        btnChooseImage = view.findViewById(R.id.btnChooseImage);
        ivProfile = view.findViewById(R.id.ivProfile);

        // Tampilkan gambar default sementara
        ivProfile.setImageResource(R.drawable.default_profile);

        // Retrieve currently logged-in user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Fetch and display current user data if available
            mDatabase.child("users").child(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String currentName = task.getResult().child("nama").getValue(String.class);
                        String currentEmail = task.getResult().child("email").getValue(String.class);
                        currentProfileImageUrl = task.getResult().child("profileImage").getValue(String.class);

                        etName.setText(currentName);
                        etEmail.setText(currentEmail);

                        // Set profile image if available
                        if (currentProfileImageUrl != null && !currentProfileImageUrl.isEmpty()) {
                            Log.d(TAG, "Memuat foto profil dari: " + currentProfileImageUrl);
                            Glide.with(getContext())
                                    .load(currentProfileImageUrl)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .circleCrop()
                                    .placeholder(R.drawable.default_profile)
                                    .error(R.drawable.default_profile)
                                    .into(ivProfile);
                        } else {
                            // Jika tidak ada gambar, tampilkan gambar default
                            ivProfile.setImageResource(R.drawable.default_profile);
                        }
                    }
                } else {
                    Log.e(TAG, "Error mengambil data pengguna: " + task.getException().getMessage());
                }
            });
        }

        // Open file chooser to select image
        btnChooseImage.setOnClickListener(v -> openFileChooser());

        // Save changes to Firebase
        btnSave.setOnClickListener(v -> updateProfile());

        return view;
    }

    private void updateProfile() {
        String newName = etName.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        // Tampilkan dialog loading
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Memperbarui profil...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            boolean hasChanges = false;

            // Update name if provided
            if (!TextUtils.isEmpty(newName)) {
                mDatabase.child("users").child(userId).child("nama").setValue(newName);
                hasChanges = true;
            }

            // Update email if provided
            if (!TextUtils.isEmpty(newEmail)) {
                mDatabase.child("users").child(userId).child("email").setValue(newEmail);
                hasChanges = true;
            }

            // Update password if both old and new passwords are provided
            if (!TextUtils.isEmpty(oldPassword) && !TextUtils.isEmpty(newPassword)) {
                user.updatePassword(newPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Kata sandi berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Gagal memperbarui kata sandi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
                hasChanges = true;
            } else if (!TextUtils.isEmpty(newPassword) && TextUtils.isEmpty(oldPassword)) {
                Toast.makeText(getActivity(), "Harap berikan kata sandi lama Anda untuk memperbarui ke kata sandi baru", Toast.LENGTH_SHORT).show();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                return;
            }

            // Upload image if selected
            if (imageUri != null) {
                uploadImageToImgBB(imageUri);
                hasChanges = true;
            } else {
                // Jika tidak ada gambar baru yang dipilih
                if (hasChanges) {
                    // Simpan URL gambar yang ada jika ada perubahan lain
                    if (currentProfileImageUrl != null && !currentProfileImageUrl.isEmpty()) {
                        mDatabase.child("users").child(userId).child("profileImage").setValue(currentProfileImageUrl);
                    }

                    // Update SharedPreferences juga
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (!TextUtils.isEmpty(newName)) {
                        editor.putString("user_name", newName);
                    }
                    if (!TextUtils.isEmpty(newEmail)) {
                        editor.putString("user_email", newEmail);
                    }
                    editor.apply();

                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getActivity(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                } else {
                    // Tidak ada perubahan
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getActivity(), "Tidak ada perubahan untuk disimpan", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(getActivity(), "Pengguna tidak ditemukan. Silakan login kembali", Toast.LENGTH_SHORT).show();
        }
    }

    // Open file chooser to pick an image
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Upload the image to ImgBB
    private void uploadImageToImgBB(Uri uri) {
        try {
            String filePath = getRealPathFromURI(uri);
            if (filePath == null) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(getActivity(), "Tidak dapat mengakses file gambar", Toast.LENGTH_SHORT).show();
                return;
            }

            File file = new File(filePath);
            if (!file.exists()) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(getActivity(), "File gambar tidak ditemukan", Toast.LENGTH_SHORT).show();
                return;
            }

            OkHttpClient client = new OkHttpClient();

            // Create request body with the image file
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", file.getName(),
                            RequestBody.create(MediaType.parse("image/*"), file))
                    .addFormDataPart("key", "8fcb52ab93dc9230c2551417e398f787")  // ImgBB API key
                    .build();

            // Create request to ImgBB API
            Request request = new Request.Builder()
                    .url("https://api.imgbb.com/1/upload")
                    .post(requestBody)
                    .build();

            // Run the request asynchronously
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Upload gambar gagal: " + e.getMessage());
                    getActivity().runOnUiThread(() -> {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getActivity(), "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.d(TAG, "Response ImgBB: " + responseBody);
                        try {
                            // Parse the JSON response from ImgBB
                            JSONObject jsonObject = new JSONObject(responseBody);
                            if (jsonObject.getBoolean("success")) {
                                String imageUrl = jsonObject.getJSONObject("data").getString("url");
                                Log.d(TAG, "URL gambar yang diunggah: " + imageUrl);

                                // Save the image URL to Firebase and SharedPreferences
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    mDatabase.child("users").child(userId).child("profileImage").setValue(imageUrl);

                                    // Update SharedPreferences juga
                                    String newName = etName.getText().toString().trim();
                                    String newEmail = etEmail.getText().toString().trim();

                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    if (!TextUtils.isEmpty(newName)) {
                                        editor.putString("user_name", newName);
                                    }
                                    if (!TextUtils.isEmpty(newEmail)) {
                                        editor.putString("user_email", newEmail);
                                    }
                                    editor.putString("profile_image_url", imageUrl);
                                    editor.apply();

                                    getActivity().runOnUiThread(() -> {
                                        if (progressDialog != null && progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                        Toast.makeText(getActivity(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                                        getActivity().onBackPressed();
                                    });
                                }
                            } else {
                                getActivity().runOnUiThread(() -> {
                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    Toast.makeText(getActivity(), "Gagal mengunggah gambar: Respon tidak valid", Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                            getActivity().runOnUiThread(() -> {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(getActivity(), "Gagal memproses respon server", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        Log.e(TAG, "Response tidak berhasil: " + response.code());
                        getActivity().runOnUiThread(() -> {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getActivity(), "Gagal mengunggah gambar: Server error " + response.code(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception pada uploadImageToImgBB: " + e.getMessage());
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(getActivity(), "Terjadi kesalahan saat mengunggah gambar", Toast.LENGTH_SHORT).show();
        }
    }

    // Get real path from URI
    private String getRealPathFromURI(Uri uri) {
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
            if (cursor == null) return null;

            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        } catch (Exception e) {
            Log.e(TAG, "Error pada getRealPathFromURI: " + e.getMessage());
            return null;
        }
    }

    // Handle the result from the file chooser (for image selection)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                // Display the selected image in the ImageView
                Glide.with(getContext())
                        .load(imageUri)
                        .circleCrop()
                        .into(ivProfile);
            } catch (Exception e) {
                Log.e(TAG, "Error showing selected image: " + e.getMessage());
                Toast.makeText(getActivity(), "Tidak dapat menampilkan gambar yang dipilih", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}