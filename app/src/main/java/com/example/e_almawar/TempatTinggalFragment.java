package com.example.e_almawar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import android.widget.ArrayAdapter;

import com.example.e_almawar.viewmodel.FormViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TempatTinggalFragment extends Fragment {

    private EditText etAlamat;
    MaterialAutoCompleteTextView etTinggalBersama, etStatusRumah;

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

        // Set adapter untuk AutoCompleteTextView Tinggal Bersama
        String[] tinggalBersamaOptions = {"Orang Tua", "Wali", "Kakek/Nenek", "Saudara", "Sendiri", "Lainnya"};
        ArrayAdapter<String> adapterTinggalBersama = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, tinggalBersamaOptions);
        etTinggalBersama.setAdapter(adapterTinggalBersama);

        // Set adapter untuk AutoCompleteTextView Status Rumah
        String[] statusRumahOptions = {"Milik Sendiri", "Kontrak", "Sewa", "Menumpang", "Asrama", "Lainnya"};
        ArrayAdapter<String> adapterStatusRumah = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, statusRumahOptions);
        etStatusRumah.setAdapter(adapterStatusRumah);

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
        // Validasi input: Periksa apakah semua field sudah diisi
        if (etAlamat.getText().toString().trim().isEmpty() ||
                etTinggalBersama.getText().toString().trim().isEmpty() ||
                etStatusRumah.getText().toString().trim().isEmpty()) {

            // Tampilkan pesan alert jika ada field yang kosong
            Toast.makeText(getActivity(), "Silahkan Isi Semua Data", Toast.LENGTH_SHORT).show();
            return; // Jika ada field kosong, data tidak disimpan
        }

        Map<String, Object> dataSiswa = new HashMap<>();

        // Data Pribadi Siswa
        dataSiswa.put("namaLengkap", formViewModel.getNamaLengkap());
        dataSiswa.put("nisn", formViewModel.getNisn());
        dataSiswa.put("nik", formViewModel.getNik());
        dataSiswa.put("tempatTanggalLahir", formViewModel.getTempatTanggalLahir());
        dataSiswa.put("jenisKelamin", formViewModel.getJenisKelamin());
        dataSiswa.put("agama", formViewModel.getAgama());
        dataSiswa.put("jumlahSaudara", formViewModel.getJumlahSaudara());
        dataSiswa.put("nomorHandphone", formViewModel.getNomorHandphone());
        dataSiswa.put("email", formViewModel.getEmail());
        dataSiswa.put("asalSekolah", formViewModel.getAsalSekolah());
        dataSiswa.put("npsn", formViewModel.getNpsn());
        dataSiswa.put("alamatSekolah", formViewModel.getAlamatSekolah());

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Ambil user ID dari FirebaseAuth

        db.collection("data_siswa")
                .document(userId)
                .set(dataSiswa)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Data berhasil disimpan!", Toast.LENGTH_SHORT).show();
                    Log.d("Firestore", "Data berhasil disimpan");

                    // Setelah data formulir berhasil disubmit, simpan status pengisian formulir
                    saveFormStatusToFirestore(userId); // Menyimpan status formulir sudah diisi

                    // Pindah ke UploadBerkasFragment setelah berhasil simpan
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new UploadBerkasFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Gagal menyimpan data!", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Gagal menyimpan data", e);
                });
    }

    private void saveFormStatusToFirestore(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> formStatus = new HashMap<>();
        formStatus.put("isFormFilled", true);  // Tandai formulir sudah diisi

        db.collection("data_siswa")
                .document(userId)
                .update(formStatus)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Status formulir berhasil diperbarui");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Gagal memperbarui status formulir", e);
                });
    }

}
