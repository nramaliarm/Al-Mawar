package com.example.e_almawar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SiswaFormulirFragment extends Fragment {
    private ViewPager2 viewPager;
    private FormPagerAdapter adapter;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_siswa_formulir, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Cek apakah user sudah isi formulir dan upload berkas
        checkIfFormIsFilled();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewPager);
        adapter = new FormPagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false); // Matikan swipe manual, hanya bisa pakai tombol
    }

    public void nextPage() {
        if (viewPager != null && viewPager.getCurrentItem() < adapter.getItemCount() - 1) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    public void previousPage() {
        if (viewPager != null && viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    // Fungsi untuk memeriksa apakah formulir sudah diisi
    private void checkIfFormIsFilled() {
        Log.d("SiswaHomeActivity", "Menjalankan checkIfFormIsFilled()");

        showLoadingFragment(); // Menampilkan LoadingFragment
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

                            // Cek apakah pengguna sudah mengunggah berkas
                            checkIfFilesAreUploaded(userId);

                        } else {
                            // Pengguna belum mengisi formulir
                            Log.d("FormCheck", "Formulir belum diisi");
                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, new SiswaFormulirFragment());
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    } else {
                        Log.d("FormCheck", "Dokumen tidak ditemukan");
                        // Data tidak ditemukan, arahkan ke formulir
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, new BiodataFragment());
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                })
                .addOnFailureListener(e -> {
                    hideLoadingFragment(); // Menyembunyikan LoadingFragment setelah gagal
                    Log.e("SiswaHomeActivity", "Gagal mengecek status formulir", e);
                    Toast.makeText(getActivity(), "Gagal mengecek status formulir", Toast.LENGTH_SHORT).show();
                });
    }

    // Fungsi untuk memeriksa apakah berkas sudah diunggah
    private void checkIfFilesAreUploaded(String userId) {

        showLoadingFragment(); // Menampilkan LoadingFragment
        Log.d("SiswaHomeActivity", "Memeriksa status berkas...");

        db.collection("data_siswa").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    hideLoadingFragment(); // Menyembunyikan LoadingFragment setelah selesai
                    if (documentSnapshot.exists()) {
                        boolean allFilesUploaded = true;

                        // Mengecek apakah semua berkas sudah diunggah
                        for (String key : new String[]{"foto_diri", "ktp", "akta", "kk", "ijazah", "ktp_ortu", "kip"}) {
                            if (documentSnapshot.getString(key) == null) {
                                allFilesUploaded = false;
                                break;
                            }
                        }

                        if (allFilesUploaded) {
                            Log.d("FormCheck", "Semua berkas sudah diunggah");
                            // Arahkan ke BerhasilDaftarFragment jika semua berkas sudah diunggah
                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, new BerhasilDaftarFragment());
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else {
                            Log.d("FormCheck", "Berkas belum lengkap");
                            // Tampilkan kembali fragment formulir
                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, new UploadBerkasFragment());
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    hideLoadingFragment();
                    Log.e("SiswaHomeActivity", "Gagal memeriksa status berkas", e);
                    Toast.makeText(getActivity(), "Gagal memeriksa status berkas", Toast.LENGTH_SHORT).show();
                });
    }


    // Fungsi untuk menampilkan LoadingFragment
    private void showLoadingFragment() {
        Log.d("SiswaHomeActivity", "Menampilkan LoadingFragment");
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new LoadingFragment(), "LOADING_FRAGMENT");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Fungsi untuk menyembunyikan LoadingFragment
    private void hideLoadingFragment() {
        Log.d("SiswaHomeActivity", "Menyembunyikan LoadingFragment");
        Fragment loadingFragment = getParentFragmentManager().findFragmentByTag("LOADING_FRAGMENT");
        if (loadingFragment != null) {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.remove(loadingFragment);
            transaction.commit();
        }
    }
}
