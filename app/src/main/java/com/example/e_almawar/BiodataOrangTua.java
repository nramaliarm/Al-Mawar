package com.example.e_almawar;

public class BiodataOrangTua {
    public String namaAyah, namaIbu, pendidikanAyah, pendidikanIbu, pekerjaanAyah, pekerjaanIbu, penghasilanAyah, penghasilanIbu, nomorHp;

    public BiodataOrangTua() {
        // Diperlukan untuk Firebase
    }

    public BiodataOrangTua(String namaAyah, String namaIbu, String pendidikanAyah, String pendidikanIbu,
                           String pekerjaanAyah, String pekerjaanIbu, String penghasilanAyah, String penghasilanIbu, String nomorHp) {
        this.namaAyah = namaAyah;
        this.namaIbu = namaIbu;
        this.pendidikanAyah = pendidikanAyah;
        this.pendidikanIbu = pendidikanIbu;
        this.pekerjaanAyah = pekerjaanAyah;
        this.pekerjaanIbu = pekerjaanIbu;
        this.penghasilanAyah = penghasilanAyah;
        this.penghasilanIbu = penghasilanIbu;
        this.nomorHp = nomorHp;
    }
}