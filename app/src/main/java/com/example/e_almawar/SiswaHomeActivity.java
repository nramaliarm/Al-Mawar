package com.example.e_almawar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SiswaHomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siswa_home);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            // Menampilkan fragment home pertama kali
            currentFragment = new SiswaHomeFragment();
            replaceFragment(currentFragment);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Ambil view dari item yang diklik
            android.view.View view = bottomNavigationView.findViewById(item.getItemId());
            if (view != null) {
                view.animate().scaleX(1.2f).scaleY(1.2f)
                        .setDuration(100)
                        .setInterpolator(new android.view.animation.OvershootInterpolator())
                        .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f)
                                .setDuration(150)
                                .start())
                        .start();
            }

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

            if (selectedFragment != null && selectedFragment != currentFragment) {
                replaceFragment(selectedFragment);
                currentFragment = selectedFragment;
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
}
