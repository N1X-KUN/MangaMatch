package com.example.mangamatch  // << this matches your actual package

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mangamatch.R

class MangaMatchIntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mangamatchlayout)

        val text = findViewById<TextView>(R.id.welcomeText)

        text.alpha = 0f
        text.animate().alpha(1f).setDuration(1500).withEndAction {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MangaMatchMainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }, 1500)
        }
    }
}
