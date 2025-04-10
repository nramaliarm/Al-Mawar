package com.example.e_almawar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class AdminSejarahFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_sejarah, container, false);

        // Ambil tombol Edit Sejarah dari XML
        Button btnEditSejarah = view.findViewById(R.id.btn_edit_sejarah);

        // Tambahkan event klik
        btnEditSejarah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new AdminEditSejarahFragment());
            }
        });

        return view;
    }

    // Fungsi untuk membuka FragmentEditSejarah
    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null); // Bisa kembali ke halaman sebelumnya
        fragmentTransaction.commit();
    }
}