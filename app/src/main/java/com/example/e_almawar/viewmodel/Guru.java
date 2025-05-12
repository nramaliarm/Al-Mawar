package com.example.e_almawar.viewmodel;

public class Guru {
    private String namaLengkap;
    private String fotoURL;
    private String jabatan;
    private String jenisKelamin;
    private String jurusan;
    private String alamat;
    private String tempatLahir;
    private String tanggalLahir;
    private String pendidikanTerakhir;
    private String status;
    private String tahunMasuk;
    private String nip;
    private String noHP;
    private String agama;

    // Konstruktor default (diperlukan oleh Firestore)
    public Guru() {}

    // Konstruktor lengkap
    public Guru(String namaLengkap, String fotoURL, String jabatan, String jenisKelamin, String jurusan,
                String alamat, String tempatLahir, String tanggalLahir, String pendidikanTerakhir,
                String status, String tahunMasuk, String nip, String noHP, String agama) {
        this.namaLengkap = namaLengkap;
        this.fotoURL = fotoURL;
        this.jabatan = jabatan;
        this.jenisKelamin = jenisKelamin;
        this.jurusan = jurusan;
        this.alamat = alamat;
        this.tempatLahir = tempatLahir;
        this.tanggalLahir = tanggalLahir;
        this.pendidikanTerakhir = pendidikanTerakhir;
        this.status = status;
        this.tahunMasuk = tahunMasuk;
        this.nip = nip;
        this.noHP = noHP;
        this.agama = agama;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getFotoURL() {
        return fotoURL;
    }

    public void setFotoURL(String fotoURL) {
        this.fotoURL = fotoURL;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTempatLahir() {
        return tempatLahir;
    }

    public void setTempatLahir(String tempatLahir) {
        this.tempatLahir = tempatLahir;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(String tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }

    public String getPendidikanTerakhir() {
        return pendidikanTerakhir;
    }

    public void setPendidikanTerakhir(String pendidikanTerakhir) {
        this.pendidikanTerakhir = pendidikanTerakhir;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTahunMasuk() {
        return tahunMasuk;
    }

    public void setTahunMasuk(String tahunMasuk) {
        this.tahunMasuk = tahunMasuk;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getNoHP() {
        return noHP;
    }

    public void setNoHP(String noHP) {
        this.noHP = noHP;
    }

    public String getAgama() {
        return agama;
    }

    public void setAgama(String agama) {
        this.agama = agama;
    }
}
