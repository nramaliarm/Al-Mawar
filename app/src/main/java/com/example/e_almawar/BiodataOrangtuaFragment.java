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

public class BiodataOrangtuaFragment extends Fragment {

    private EditText etNamaAyah, etPendidikanAyah, etPekerjaanAyah, etPenghasilanAyah;
    private EditText etNamaIbu, etPendidikanIbu, etPekerjaanIbu, etPenghasilanIbu;
    private EditText etNomorHp;
    private Button btnNext, btnPrev;
    private FormViewModel formViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orangtua, container, false);

        // Inisialisasi ViewModel
        formViewModel = new ViewModelProvider(requireActivity()).get(FormViewModel.class);

        // Inisialisasi EditText Orang Tua
        etNamaAyah = view.findViewById(R.id.etNamaAyah);
        etPendidikanAyah = view.findViewById(R.id.etPendidikanAyah);
        etPekerjaanAyah = view.findViewById(R.id.etPekerjaanAyah);
        etPenghasilanAyah = view.findViewById(R.id.etPenghasilanAyah);
        etNamaIbu = view.findViewById(R.id.etNamaIbu);
        etPendidikanIbu = view.findViewById(R.id.etPendidikanIbu);
        etPekerjaanIbu = view.findViewById(R.id.etPekerjaanIbu);
        etPenghasilanIbu = view.findViewById(R.id.etPenghasilanIbu);
        etNomorHp = view.findViewById(R.id.etNomorHp);

        // Mengisi ulang data jika sudah ada di ViewModel
        etNamaAyah.setText(formViewModel.namaAyah);
        etPendidikanAyah.setText(formViewModel.pendidikanAyah);
        etPekerjaanAyah.setText(formViewModel.pekerjaanAyah);
        etPenghasilanAyah.setText(formViewModel.penghasilanAyah);
        etNamaIbu.setText(formViewModel.namaIbu);
        etPendidikanIbu.setText(formViewModel.pendidikanIbu);
        etPekerjaanIbu.setText(formViewModel.pekerjaanIbu);
        etPenghasilanIbu.setText(formViewModel.penghasilanIbu);
        etNomorHp.setText(formViewModel.nomorHp);

        // Inisialisasi tombol
        btnNext = view.findViewById(R.id.btn_next);
        btnPrev = view.findViewById(R.id.btn_prev);

        // Tombol Next ke halaman berikutnya
        btnNext.setOnClickListener(v -> {
            simpanDataKeViewModel();
            pindahKeHalamanSelanjutnya();
        });

        // Tombol Previous kembali ke halaman sebelumnya
        btnPrev.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    private void simpanDataKeViewModel() {
        formViewModel.namaAyah = etNamaAyah.getText().toString().trim();
        formViewModel.pendidikanAyah = etPendidikanAyah.getText().toString().trim();
        formViewModel.pekerjaanAyah = etPekerjaanAyah.getText().toString().trim();
        formViewModel.penghasilanAyah = etPenghasilanAyah.getText().toString().trim();
        formViewModel.namaIbu = etNamaIbu.getText().toString().trim();
        formViewModel.pendidikanIbu = etPendidikanIbu.getText().toString().trim();
        formViewModel.pekerjaanIbu = etPekerjaanIbu.getText().toString().trim();
        formViewModel.penghasilanIbu = etPenghasilanIbu.getText().toString().trim();
        formViewModel.nomorHp = etNomorHp.getText().toString().trim();
    }

    private void pindahKeHalamanSelanjutnya() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new WaliFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}