package com.example.e_almawar;

import android.util.Log;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SiswaHomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siswa_home);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            // Jika user tidak ditemukan, arahkan ke Login
            startActivity(new Intent(this, LoginAdminActivity.class));
            finish();
            return;
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            // Menampilkan fragment home pertama kali
            replaceFragment(new SiswaHomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.home) {
                selectedFragment = new SiswaHomeFragment();
            } else if (item.getItemId() == R.id.school) {
                selectedFragment = new SiswaSchoolFragment();
            } else if (item.getItemId() == R.id.formulir) {
                // Panggil checkIfFormIsFilled saat menu formulir dipilih
                checkIfFormIsFilled();
                return true; // Jangan ganti fragment langsung disini
            } else if (item.getItemId() == R.id.akun) {
                selectedFragment = new SiswaAkunFragment();
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            } else {
                Toast.makeText(this, "Fragment tidak valid", Toast.LENGTH_SHORT).show();
            }

            return true;
        });
    }

    // Fungsi untuk mengganti fragment
    public void replaceFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Fungsi untuk memeriksa apakah formulir sudah diisi
    private void checkIfFormIsFilled() {
        Log.d("SiswaHomeActivity", "Menjalankan checkIfFormIsFilled()");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Ambil user ID

        db.collection("data_siswa").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d("SiswaHomeActivity", "Dokumen ditemukan");
                    hideLoadingFragment(); // Menyembunyikan LoadingFragment setelah selesai
                    if (documentSnapshot.exists()) {
                        Boolean isFormFilled = documentSnapshot.getBoolean("isFormFilled");
                        Log.d("FormCheck", "Status formulir: " + (isFormFilled != null ? isFormFilled : "null"));
                        if (isFormFilled != null && isFormFilled) {
                            // Pengguna sudah mengisi formulir
                            Log.d("FormCheck", "Formulir sudah diisi");
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, new UploadBerkasFragment());
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else {
                            // Pengguna belum mengisi formulir
                            Log.d("FormCheck", "Formulir belum diisi");
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, new SiswaFormulirFragment());
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    } else {
                        Log.d("FormCheck", "Dokumen tidak ditemukan");
                        // Data tidak ditemukan, arahkan ke formulir
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, new SiswaFormulirFragment());
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                })
                .addOnFailureListener(e -> {
                    hideLoadingFragment(); // Menyembunyikan LoadingFragment setelah gagal
                    Log.e("SiswaHomeActivity", "Gagal mengecek status formulir", e);
                    Toast.makeText(getApplicationContext(), "Gagal mengecek status formulir", Toast.LENGTH_SHORT).show();
                });
    }

    // Fungsi untuk menampilkan LoadingFragment
    private void showLoadingFragment() {
        // Menampilkan LoadingFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new LoadingFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Fungsi untuk menyembunyikan LoadingFragment
    private void hideLoadingFragment() {
        // Menyembunyikan LoadingFragment dan langsung ke BerhasilDaftarFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new BerhasilDaftarFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
