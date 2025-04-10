package com.example.e_almawar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_almawar.viewmodel.FormViewModel;

public class WaliFragment extends Fragment {

    private EditText etNamaWali, etPendidikanWali, etPekerjaanWali, etPenghasilanWali, etNomorHandphoneWali;
    private Button btnNext, btnPrev;
    private FormViewModel formViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wali, container, false);

        // Inisialisasi ViewModel
        formViewModel = new ViewModelProvider(requireActivity()).get(FormViewModel.class);

        // Inisialisasi EditText Wali
        etNamaWali = view.findViewById(R.id.etNamaWali);
        etPendidikanWali = view.findViewById(R.id.etPendidikanWali);
        etPekerjaanWali = view.findViewById(R.id.etPekerjaanWali);
        etPenghasilanWali = view.findViewById(R.id.etPenghasilanWali);
        etNomorHandphoneWali = view.findViewById(R.id.etNomorHandphoneWali);

        // Mengisi ulang data jika sudah ada di ViewModel
        etNamaWali.setText(formViewModel.namaWali);
        etPendidikanWali.setText(formViewModel.pendidikanWali);
        etPekerjaanWali.setText(formViewModel.pekerjaanWali);
        etPenghasilanWali.setText(formViewModel.penghasilanWali);
        etNomorHandphoneWali.setText(formViewModel.nomorHandphoneWali);

        // Inisialisasi tombol
        btnNext = view.findViewById(R.id.btn_next);
        btnPrev = view.findViewById(R.id.btn_prev);

        // Tombol Next ke halaman berikutnya
        btnNext.setOnClickListener(v -> {
            simpanData();
            pindahKeHalamanSelanjutnya();
        });

        // Tombol Previous kembali ke halaman sebelumnya
        btnPrev.setOnClickListener(v -> {
            simpanData();
            pindahKeHalamanSebelumnya();
        });

        return view;
    }

    private void simpanData() {
        formViewModel.namaWali = etNamaWali.getText().toString().trim();
        formViewModel.pendidikanWali = etPendidikanWali.getText().toString().trim();
        formViewModel.pekerjaanWali = etPekerjaanWali.getText().toString().trim();
        formViewModel.penghasilanWali = etPenghasilanWali.getText().toString().trim();
        formViewModel.nomorHandphoneWali = etNomorHandphoneWali.getText().toString().trim();
    }

    private void pindahKeHalamanSelanjutnya() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new TempatTinggalFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void pindahKeHalamanSebelumnya() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new OrangtuaFragment()); // Ganti dengan fragment sebelumnya
        transaction.addToBackStack(null);
        transaction.commit();
    }
}