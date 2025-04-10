package com.example.e_almawar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SiswaHomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siswa_home);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Log.e("SiswaHomeActivity", "User tidak ditemukan! Kembali ke Login.");
            startActivity(new Intent(this, LoginAdminActivity.class));
            finish();
            return;
        } else {
            Log.d("SiswaHomeActivity", "User masih login: " + user.getEmail());
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            replaceFragment(new SiswaHomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.home) {
                selectedFragment = new SiswaHomeFragment();
            } else if (item.getItemId() == R.id.school) {
                selectedFragment = new SiswaSchoolFragment();
            } else if (item.getItemId() == R.id.formulir) {
                selectedFragment = new SiswaFormulirFragment();
            } else if (item.getItemId() == R.id.akun) {
                selectedFragment = new SiswaAkunFragment();
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            } else {
                Log.e("SiswaHomeActivity", "Fragment yang dipilih null!");
            }

            return true;
        });
    }

    public void replaceFragment(Fragment fragment) {
        if (fragment == null) {
            Log.e("SiswaHomeActivity", "replaceFragment: Fragment yang dikirimkan null!");
            return;
        }

        Log.d("SiswaHomeActivity", "Mengganti fragment ke: " + fragment.getClass().getSimpleName());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}