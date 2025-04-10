package com.example.e_almawar;

public class BiodataSiswa {
    public String namaLengkap, nisn, nik, tempatTanggalLahir, jenisKelamin,
            agama, jumlahSaudara, nomorHandphone, email, asalSekolah, npsn, alamatSekolah;

    // Konstruktor kosong diperlukan untuk Firebase
    public BiodataSiswa() {
    }

    public BiodataSiswa(String namaLengkap, String nisn, String nik, String tempatTanggalLahir, String jenisKelamin,
                        String agama, String jumlahSaudara, String nomorHandphone, String email,
                        String asalSekolah, String npsn, String alamatSekolah) {
        this.namaLengkap = namaLengkap;
        this.nisn = nisn;
        this.nik = nik;
        this.tempatTanggalLahir = tempatTanggalLahir;
        this.jenisKelamin = jenisKelamin;
        this.agama = agama;
        this.jumlahSaudara = jumlahSaudara;
        this.nomorHandphone = nomorHandphone;
        this.email = email;
        this.asalSekolah = asalSekolah;
        this.npsn = npsn;
        this.alamatSekolah = alamatSekolah;
    }
}