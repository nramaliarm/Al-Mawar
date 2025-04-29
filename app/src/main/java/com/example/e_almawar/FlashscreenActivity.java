package com.example.e_almawar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

// Pastikan MainActivity diimpor jika berada di paket yang berbeda
import com.example.e_almawar.MainActivity;

public class FlashscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashscreen);

        // Menangani hasil dari sign-in intent
        // Menggunakan Handler untuk menunda eksekusi selama 6 detik
        new Handler().postDelayed(() -> {
            // Intent untuk berpindah ke MainActivity setelah 6 detik
            Intent intent = new Intent(FlashscreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Supaya tidak bisa kembali ke Flashscreen setelah berpindah
            // Animasi transisi antara FlashscreenActivity dan MainActivity
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 6000); // Delay selama 6 detik
    }

    // Method test() untuk menulis pesan ke database (opsional)
    private void test() {
        // Menulis pesan ke database
        // FirebaseDatabase database = FirebaseDatabase.getInstance();
        // DatabaseReference myRef = database.getReference("message");
        // myRef.setValue("Hello, World!");
    }
}
