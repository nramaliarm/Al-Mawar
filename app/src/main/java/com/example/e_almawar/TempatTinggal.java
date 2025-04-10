package com.example.e_almawar;

public class TempatTinggal {
    public String alamat, tinggalBersama, statusRumah;

    // Konstruktor kosong diperlukan untuk Firebase
    public TempatTinggal() {
    }

    public TempatTinggal(String alamat, String tinggalBersama, String statusRumah) {
        this.alamat = alamat;
        this.tinggalBersama = tinggalBersama;
        this.statusRumah = statusRumah;
    }
}