package com.example.e_almawar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_almawar.viewmodel.FormViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TempatTinggalFragment extends Fragment {

    private EditText etAlamat, etTinggalBersama, etStatusRumah;
    private Button btnSubmit, btnPrev;
    private FormViewModel formViewModel;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tempat_tinggal, container, false);

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();

        // Inisialisasi ViewModel
        formViewModel = new ViewModelProvider(requireActivity()).get(FormViewModel.class);

        // Inisialisasi input tempat tinggal
        etAlamat = view.findViewById(R.id.etAlamat);
        etTinggalBersama = view.findViewById(R.id.etTinggalBersama);
        etStatusRumah = view.findViewById(R.id.etStatusRumah);

        // Mengisi ulang data jika sudah ada di ViewModel
        etAlamat.setText(formViewModel.alamat);
        etTinggalBersama.setText(formViewModel.tinggalbersama);
        etStatusRumah.setText(formViewModel.statusrumah);

        // Inisialisasi tombol
        btnPrev = view.findViewById(R.id.btn_prev);
        btnSubmit = view.findViewById(R.id.btn_submit);

        // Tombol Previous kembali ke WaliFragment
        btnPrev.setOnClickListener(v -> pindahKeWaliFragment());

        // Tombol Submit untuk menyimpan ke database
        btnSubmit.setOnClickListener(v -> {
            simpanData();
            submitDataKeDatabase();
        });

        return view;
    }

    private void simpanData() {
        formViewModel.alamat = etAlamat.getText().toString().trim();
        formViewModel.tinggalbersama = etTinggalBersama.getText().toString().trim();
        formViewModel.statusrumah = etStatusRumah.getText().toString().trim();
    }

    private void pindahKeWaliFragment() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new WaliFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void submitDataKeDatabase() {
        Map<String, Object> dataSiswa = new HashMap<>();

        // Data Pribadi Siswa
        dataSiswa.put("namaLengkap", formViewModel.namaLengkap);
        dataSiswa.put("nisn", formViewModel.nisn);
        dataSiswa.put("nik", formViewModel.nik);
        dataSiswa.put("tempatTanggalLahir", formViewModel.tempatTanggalLahir);
        dataSiswa.put("jenisKelamin", formViewModel.jenisKelamin);
        dataSiswa.put("agama", formViewModel.agama);
        dataSiswa.put("jumlahSaudara", formViewModel.jumlahSaudara);
        dataSiswa.put("nomorHandphone", formViewModel.nomorHandphone);
        dataSiswa.put("email", formViewModel.email);
        dataSiswa.put("asalSekolah", formViewModel.asalSekolah);
        dataSiswa.put("npsn", formViewModel.npsn);
        dataSiswa.put("alamatSekolah", formViewModel.alamatSekolah);

        // Data Orang Tua & Wali
        dataSiswa.put("namaAyah", formViewModel.namaAyah);
        dataSiswa.put("pendidikanAyah", formViewModel.pendidikanAyah);
        dataSiswa.put("pekerjaanAyah", formViewModel.pekerjaanAyah);
        dataSiswa.put("penghasilanAyah", formViewModel.penghasilanAyah);
        dataSiswa.put("namaIbu", formViewModel.namaIbu);
        dataSiswa.put("pendidikanIbu", formViewModel.pendidikanIbu);
        dataSiswa.put("pekerjaanIbu", formViewModel.pekerjaanIbu);
        dataSiswa.put("penghasilanIbu", formViewModel.penghasilanIbu);
        dataSiswa.put("namaWali", formViewModel.namaWali);
        dataSiswa.put("pendidikanWali", formViewModel.pendidikanWali);
        dataSiswa.put("pekerjaanWali", formViewModel.pekerjaanWali);
        dataSiswa.put("penghasilanWali", formViewModel.penghasilanWali);
        dataSiswa.put("nomorHandphoneWali", formViewModel.nomorHandphoneWali);

        // Data Tempat Tinggal
        dataSiswa.put("alamat", formViewModel.alamat);
        dataSiswa.put("tinggalBersama", formViewModel.tinggalbersama);
        dataSiswa.put("statusRumah", formViewModel.statusrumah);

        db.collection("data_siswa")
                .add(dataSiswa)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getActivity(), "Data berhasil disimpan!", Toast.LENGTH_SHORT).show();
                    Log.d("Firestore", "Data berhasil disimpan dengan ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Gagal menyimpan data!", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Gagal menyimpan data", e);
                });
    }
}