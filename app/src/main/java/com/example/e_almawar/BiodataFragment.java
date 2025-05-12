package com.example.e_almawar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_almawar.viewmodel.FormViewModel;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.Calendar;
import java.util.Locale;

public class BiodataFragment extends Fragment {

    private EditText etNamaLengkap, etNisn, etNik, etTempatTanggalLahir;
    private EditText etJumlahSaudara, etNomorHandphone, etEmail, etAsalSekolah, etNpsn, etAlamatSekolah;
    private MaterialAutoCompleteTextView etJenisKelamin, etAgama;
    private Button btnNext;
    private FormViewModel formViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_biodata, container, false);

        // Inisialisasi ViewModel
        formViewModel = new ViewModelProvider(requireActivity()).get(FormViewModel.class);

        // Inisialisasi EditText dan AutoComplete
        etNamaLengkap = view.findViewById(R.id.etNamaLengkap);
        etNisn = view.findViewById(R.id.etNisn);
        etNik = view.findViewById(R.id.etNik);
        etTempatTanggalLahir = view.findViewById(R.id.etTanggalLahir);
        etJenisKelamin = view.findViewById(R.id.etJenisKelamin);
        etAgama = view.findViewById(R.id.etAgama);
        etJumlahSaudara = view.findViewById(R.id.etJumlahSaudara);
        etNomorHandphone = view.findViewById(R.id.etNomorHandphone);
        etEmail = view.findViewById(R.id.etEmail);
        etAsalSekolah = view.findViewById(R.id.etAsalSekolah);
        etNpsn = view.findViewById(R.id.etNpsn);
        etAlamatSekolah = view.findViewById(R.id.etAlamatSekolah);

        // Nonaktifkan input manual & buka kalender saat diklik
        etTempatTanggalLahir.setInputType(InputType.TYPE_NULL);
        etTempatTanggalLahir.setFocusable(false);
        etTempatTanggalLahir.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String tanggal = String.format(Locale.getDefault(), "%02d-%02d-%04d",
                                selectedDay, selectedMonth + 1, selectedYear);
                        etTempatTanggalLahir.setText(tanggal);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Set adapter Jenis Kelamin
        String[] jenisKelaminOptions = {"Laki-Laki", "Perempuan"};
        ArrayAdapter<String> adapterJenisKelamin = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, jenisKelaminOptions);
        etJenisKelamin.setAdapter(adapterJenisKelamin);

        // Set adapter Agama
        String[] agamaOptions = {"Islam", "Kristen", "Katolik", "Hindu", "Buddha", "Konghucu"};
        ArrayAdapter<String> adapterAgama = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, agamaOptions);
        etAgama.setAdapter(adapterAgama);

        // Set nilai dari ViewModel
        etNamaLengkap.setText(formViewModel.getNamaLengkap());
        etNisn.setText(formViewModel.getNisn());
        etNik.setText(formViewModel.getNik());
        etTempatTanggalLahir.setText(formViewModel.getTempatTanggalLahir());
        etJenisKelamin.setText(formViewModel.getJenisKelamin());
        etAgama.setText(formViewModel.getAgama());
        etJumlahSaudara.setText(formViewModel.getJumlahSaudara());
        etNomorHandphone.setText(formViewModel.getNomorHandphone());
        etEmail.setText(formViewModel.getEmail());
        etAsalSekolah.setText(formViewModel.getAsalSekolah());
        etNpsn.setText(formViewModel.getNpsn());
        etAlamatSekolah.setText(formViewModel.getAlamatSekolah());

        // Simpan otomatis ke ViewModel saat mengetik
        addTextWatchers();

        // Tombol next
        btnNext = view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> pindahKeHalamanSelanjutnya());

        return view;
    }

    private void addTextWatchers() {
        etNamaLengkap.addTextChangedListener(new SimpleTextWatcher(s -> formViewModel.setNamaLengkap(s)));
        etNisn.addTextChangedListener(new SimpleTextWatcher(s -> formViewModel.setNisn(s)));
        etNik.addTextChangedListener(new SimpleTextWatcher(s -> formViewModel.setNik(s)));
        etTempatTanggalLahir.addTextChangedListener(new SimpleTextWatcher(s -> formViewModel.setTempatTanggalLahir(s)));
        etJenisKelamin.addTextChangedListener(new SimpleTextWatcher(s -> formViewModel.setJenisKelamin(s)));
        etAgama.addTextChangedListener(new SimpleTextWatcher(s -> formViewModel.setAgama(s)));
        etJumlahSaudara.addTextChangedListener(new SimpleTextWatcher(s -> formViewModel.setJumlahSaudara(s)));
        etNomorHandphone.addTextChangedListener(new SimpleTextWatcher(s -> formViewModel.setNomorHandphone(s)));
        etEmail.addTextChangedListener(new SimpleTextWatcher(s -> formViewModel.setEmail(s)));
        etAsalSekolah.addTextChangedListener(new SimpleTextWatcher(s -> formViewModel.setAsalSekolah(s)));
        etNpsn.addTextChangedListener(new SimpleTextWatcher(s -> formViewModel.setNpsn(s)));
        etAlamatSekolah.addTextChangedListener(new SimpleTextWatcher(s -> formViewModel.setAlamatSekolah(s)));
    }

    private void pindahKeHalamanSelanjutnya() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new BiodataOrangtuaFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // TextWatcher ringkas
    private static class SimpleTextWatcher implements TextWatcher {
        private final TextCallback callback;

        public SimpleTextWatcher(TextCallback callback) {
            this.callback = callback;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            callback.onTextChanged(s.toString());
        }

        interface TextCallback {
            void onTextChanged(String newText);
        }
    }
}
