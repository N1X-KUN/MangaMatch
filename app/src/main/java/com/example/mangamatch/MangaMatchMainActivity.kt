package com.example.mangamatch

import android.os.Build
import android.os.Bundle
import android.view.View
import android.graphics.RenderEffect
import android.graphics.Shader
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.LinearLayout

class MangaMatchMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mangamatchmain)

        val popupLayout = findViewById<LinearLayout>(R.id.instructionPopup)
        val closePopupButton = findViewById<Button>(R.id.closePopupButton)
        val showPopupButton = findViewById<Button>(R.id.showPopupButton)
        val blurContainer = findViewById<View>(R.id.blurContainer)

        // Initially show the popup and blur the background
        popupLayout.visibility = View.VISIBLE
        showPopupButton.visibility = View.GONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurContainer.setRenderEffect(RenderEffect.createBlurEffect(10f, 10f, Shader.TileMode.CLAMP))
        }

        // When "Got it!" is clicked, hide the popup and remove the blur from the main content while showing the toggle button
        closePopupButton.setOnClickListener {
            popupLayout.visibility = View.GONE
            showPopupButton.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                blurContainer.setRenderEffect(null)
            }
        }

        // When the toggle button is clicked, show the popup again and apply the blur to the main content
        showPopupButton.setOnClickListener {
            popupLayout.visibility = View.VISIBLE
            showPopupButton.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                blurContainer.setRenderEffect(RenderEffect.createBlurEffect(10f, 10f, Shader.TileMode.CLAMP))
            }
        }
    }
}