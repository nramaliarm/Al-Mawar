package com.example.e_almawar.viewmodel;

import androidx.lifecycle.ViewModel;

public class FormViewModel extends ViewModel {
    // Data Pribadi Siswa
    public String namaLengkap;
    public String nisn;
    public String nik;
    public String tempatTanggalLahir;
    public String jenisKelamin;
    public String agama;
    public String jumlahSaudara;
    public String nomorHandphone;
    public String email;
    public String asalSekolah;
    public String npsn;
    public String alamatSekolah;

    // Data Orang Tua - Ayah
    public String namaAyah;
    public String pendidikanAyah;
    public String pekerjaanAyah;
    public String penghasilanAyah;

    // Data Orang Tua - Ibu
    public String namaIbu;
    public String pendidikanIbu;
    public String pekerjaanIbu;
    public String penghasilanIbu;
    public String nomorHp;


    // Data Wali
    public String namaWali;
    public String pendidikanWali;
    public String pekerjaanWali;
    public String penghasilanWali;
    public String nomorHandphoneWali;

    // Data Tempat Tinggal
    public String alamat;
    public String tinggalbersama;
    public String statusrumah;


    // Setter dan Getter untuk Data Pribadi Siswa
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    public String getNamaLengkap() { return namaLengkap; }

    public void setNisn(String nisn) { this.nisn = nisn; }
    public String getNisn() { return nisn; }

    public void setNik(String nik) { this.nik = nik; }
    public String getNik() { return nik; }

    public void setTempatTanggalLahir(String tempatTanggalLahir) { this.tempatTanggalLahir = tempatTanggalLahir; }
    public String getTempatTanggalLahir() { return tempatTanggalLahir; }

    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }
    public String getJenisKelamin() { return jenisKelamin; }

    public void setAgama(String agama) { this.agama = agama; }
    public String getAgama() { return agama; }

    public void setJumlahSaudara(String jumlahSaudara) { this.jumlahSaudara = jumlahSaudara; }
    public String getJumlahSaudara() { return jumlahSaudara; }

    public void setNomorHandphone(String nomorHandphone) { this.nomorHandphone = nomorHandphone; }
    public String getNomorHandphone() { return nomorHandphone; }

    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }

    public void setAsalSekolah(String asalSekolah) { this.asalSekolah = asalSekolah; }
    public String getAsalSekolah() { return asalSekolah; }

    public void setNpsn(String npsn) { this.npsn = npsn; }
    public String getNpsn() { return npsn; }

    public void setAlamatSekolah(String alamatSekolah) { this.alamatSekolah = alamatSekolah; }
    public String getAlamatSekolah() { return alamatSekolah; }

    // Setter dan Getter untuk Data Orang Tua - Ayah
    public void setNamaAyah(String namaAyah) { this.namaAyah = namaAyah; }
    public String getNamaAyah() { return namaAyah; }

    public void setPendidikanAyah(String pendidikanAyah) { this.pendidikanAyah = pendidikanAyah; }
    public String getPendidikanAyah() { return pendidikanAyah; }

    public void setPekerjaanAyah(String pekerjaanAyah) { this.pekerjaanAyah = pekerjaanAyah; }
    public String getPekerjaanAyah() { return pekerjaanAyah; }

    public void setPenghasilanAyah(String penghasilanAyah) { this.penghasilanAyah = penghasilanAyah; }
    public String getPenghasilanAyah() { return penghasilanAyah; }

    // Setter dan Getter untuk Data Orang Tua - Ibu
    public void setNamaIbu(String namaIbu) { this.namaIbu = namaIbu; }
    public String getNamaIbu() { return namaIbu; }

    public void setPendidikanIbu(String pendidikanIbu) { this.pendidikanIbu = pendidikanIbu; }
    public String getPendidikanIbu() { return pendidikanIbu; }

    public void setPekerjaanIbu(String pekerjaanIbu) { this.pekerjaanIbu = pekerjaanIbu; }
    public String getPekerjaanIbu() { return pekerjaanIbu; }

    public void setPenghasilanIbu(String penghasilanIbu) { this.penghasilanIbu = penghasilanIbu; }
    public String getPenghasilanIbu() { return penghasilanIbu; }

    public void setNomorHp(String nomorHp) { this.nomorHp = nomorHp; }
    public String getNomorHp() { return nomorHp; }

    // Setter dan Getter untuk Data Wali
    public void setNamaWali(String namaWali) { this.namaWali = namaWali; }
    public String getNamaWali() { return namaWali; }

    public void setPendidikanWali(String pendidikanWali) { this.pendidikanWali = pendidikanWali; }
    public String getPendidikanWali() { return pendidikanWali; }

    public void setPekerjaanWali(String pekerjaanWali) { this.pekerjaanWali = pekerjaanWali; }
    public String getPekerjaanWali() { return pekerjaanWali; }

    public void setPenghasilanWali(String penghasilanWali) { this.penghasilanWali = penghasilanWali; }
    public String getPenghasilanWali() { return penghasilanWali; }

    public void setNomorHandphoneWali(String nomorHandphoneWali) { this.nomorHandphoneWali = nomorHandphoneWali; }
    public String getNomorHandphoneWali() { return nomorHandphoneWali; }

    // Setter dan Getter untuk Data Tempat Tinggal
    public void setAlamat(String alamat) { this.alamat = alamat; }
    public String getAlamat() { return alamat; }
    public void setTinggalBersama(String tinggalBersama) { this.tinggalbersama = tinggalBersama; }
    public String getTinggalBersama() { return tinggalbersama; }
    public void setStatusRumah(String statusRumah) { this.statusrumah = statusRumah; }
    public String getStatusRumah() { return statusrumah; }

}