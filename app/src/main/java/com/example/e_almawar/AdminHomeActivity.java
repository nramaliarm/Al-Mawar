package com.example.e_almawar;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminHomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Inisialisasi BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Tampilkan fragment default (AdminHomeFragment)
        if (savedInstanceState == null) {
            replaceFragment(new AdminHomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.home) {
                selectedFragment = new AdminHomeFragment();
            } else if (item.getItemId() == R.id.school) {
                selectedFragment = new AdminSchoolFragment();
            } else if (item.getItemId() == R.id.formulir) {
                selectedFragment = new AdminFormulirFragment();
            } else if (item.getItemId() == R.id.akun) {
                selectedFragment = new AdminAkunFragment();
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }

            return true;
        });
    }

    // Metode untuk mengganti fragment
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment); // Pastikan ID ini sesuai dengan XML
        fragmentTransaction.commit();
    }
}
