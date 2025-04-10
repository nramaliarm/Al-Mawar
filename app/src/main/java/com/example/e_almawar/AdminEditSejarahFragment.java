package com.example.e_almawar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class AdminEditSejarahFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_edit_sejarah, container, false);

        // Ambil tombol dari XML
        Button btnBatal = view.findViewById(R.id.btn_batal);
        Button btnSimpan = view.findViewById(R.id.btn_simpan);

        // Saat tombol ditekan, kembali ke AdminSejarahFragment
        View.OnClickListener kembaliKeSejarah = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kembaliKeAdminSejarah();
            }
        };

        btnBatal.setOnClickListener(kembaliKeSejarah);
        btnSimpan.setOnClickListener(kembaliKeSejarah);

        return view;
    }

    // 🔥 Fungsi untuk kembali ke AdminSejarahFragment
    private void kembaliKeAdminSejarah() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new AdminSejarahFragment()); // Ganti fragment
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
