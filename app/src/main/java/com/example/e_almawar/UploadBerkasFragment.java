package com.example.e_almawar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONObject;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadBerkasFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private String currentFieldKey;
    private final Map<String, Uri> selectedImageUris = new HashMap<>();
    private final Map<String, EditText> fieldEditTexts = new HashMap<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private final String[] fieldKeys = {
            "foto_diri", "ktp", "akta", "kk", "ijazah", "ktp_ortu", "kip"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_berkas, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Cek apakah user sudah upload semua berkas
        checkAllFieldsUploaded();

        int[] buttonIds = {
                R.id.btn_foto_diri, R.id.btn_ktp, R.id.btn_akta, R.id.btn_kk,
                R.id.btn_ijazah, R.id.btn_ktp_ortu, R.id.btn_kip
        };

        int[] editTextIds = {
                R.id.et_foto_diri, R.id.et_ktp, R.id.et_akta, R.id.et_kk,
                R.id.et_ijazah, R.id.et_ktp_ortu, R.id.et_kip
        };

        for (int i = 0; i < fieldKeys.length; i++) {
            String key = fieldKeys[i];
            Button button = view.findViewById(buttonIds[i]);
            EditText editText = view.findViewById(editTextIds[i]);
            fieldEditTexts.put(key, editText);

            button.setOnClickListener(v -> {
                currentFieldKey = key;
                selectImage();
            });
        }

        Button uploadButton = view.findViewById(R.id.btn_upload_berkas);
        uploadButton.setOnClickListener(v -> uploadAllImages());

        return view;
    }


    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            selectedImageUris.put(currentFieldKey, imageUri);
            String fileName = getFileName(imageUri);
            fieldEditTexts.get(currentFieldKey).setText(fileName);
        }
    }

    private String getFileName(Uri uri) {
        String result = "gambar.jpg";
        Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (cursor.moveToFirst()) {
                result = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return result;
    }

    private void uploadAllImages() {
        if (selectedImageUris.isEmpty()) {
            Toast.makeText(getContext(), "Silakan pilih minimal 1 gambar.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> uploadedUrls = new HashMap<>();
        final int[] counter = {0};

        for (String key : selectedImageUris.keySet()) {
            Uri imageUri = selectedImageUris.get(key);
            uploadToImgur(imageUri, new ImgurCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    uploadedUrls.put(key, imageUrl);
                    counter[0]++;
                    if (counter[0] == selectedImageUris.size()) {
                        saveToFirestore(uploadedUrls);
                        checkAllFieldsUploaded();  // Periksa status upload berkas
                    }
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(getContext(), "Upload gagal: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadToImgur(Uri imageUri, ImgurCallback callback) {
        try {
            InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = getBytes(inputStream);
            String base64Image = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", base64Image)
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.imgur.com/3/image")
                    .addHeader("Authorization", "Client-ID 26b0a70ffb1b617")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    requireActivity().runOnUiThread(() -> callback.onFailure(e.getMessage()));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        String responseData = response.body().string();
                        JSONObject json = new JSONObject(responseData);
                        String imageUrl = json.getJSONObject("data").getString("link");
                        requireActivity().runOnUiThread(() -> callback.onSuccess(imageUrl));
                    } catch (Exception e) {
                        requireActivity().runOnUiThread(() -> callback.onFailure("Parsing error"));
                    }
                }
            });

        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    private byte[] getBytes(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void saveToFirestore(Map<String, String> data) {
        Map<String, Object> convertedData = new HashMap<>();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            convertedData.put(entry.getKey(), entry.getValue());
        }

        String userId = auth.getCurrentUser().getUid();
        db.collection("data_siswa").document(userId)
                .update(convertedData)
                .addOnSuccessListener(aVoid -> {
                    View rootView = getView();
                    if (rootView != null) {
                        Toast.makeText(getActivity(), "Berkas berhasil diupload!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkAllFieldsUploaded() {
        showLoadingFragment();  // Tampilkan fragment loading

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Ambil data berkas dari Firestore
        db.collection("data_siswa").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Mengecek apakah semua berkas telah diupload
                        boolean allFieldsUploaded = true;
                        for (String key : fieldKeys) {
                            if (documentSnapshot.getString(key) == null) {
                                allFieldsUploaded = false;
                                break;
                            }
                        }

                        // Arahkan ke FragmentBerhasilDaftar jika semua berkas sudah diupload
                        if (allFieldsUploaded) {
                            // Sembunyikan loading dan tampilkan BerhasilDaftarFragment
                            hideLoadingFragment();
                        } else {
                            // Sembunyikan loading dan tampilkan UploadBerkasFragment
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, new UploadBerkasFragment());
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal memeriksa status berkas.", Toast.LENGTH_SHORT).show();
                });
    }

    private void showLoadingFragment() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new LoadingFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void hideLoadingFragment() {
        // Ganti fragment loading dengan fragment berhasil daftar
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new BerhasilDaftarFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private interface ImgurCallback {
        void onSuccess(String imageUrl);
        void onFailure(String error);
    }
}
