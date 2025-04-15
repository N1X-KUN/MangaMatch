package com.example.mangamatch

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MangaMatchMainActivity : AppCompatActivity() {

    private lateinit var blurContainer: View
    private lateinit var dimOverlay: View
    private lateinit var instructionPopup: View
    private lateinit var showPopupButton: Button
    private lateinit var moodPopup: View
    private lateinit var emotionGrid: GridLayout
    private lateinit var chooseMoodBtn: Button
    private val selectedEmotions = mutableListOf<String>()

    private val emotions = listOf(
        "Happy", "Sad", "Bored",
        "Depressed", "Lazy", "Hungry",
        "Lovestruck", "Scared", "Surprise Me"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mangamatchmain)

        // Find Views
        blurContainer = findViewById(R.id.blurContainer)
        dimOverlay = findViewById(R.id.dimOverlay)
        instructionPopup = findViewById(R.id.instructionPopupWrapper)
        moodPopup = findViewById(R.id.moodPopupWrapper)
        showPopupButton = findViewById(R.id.showPopupButton)
        emotionGrid = findViewById(R.id.emotionGrid)
        chooseMoodBtn = findViewById(R.id.chooseMoodBtn)

        // Setup emotion buttons
        setupEmotionButtons()

        // Instruction popup close
        val closePopupBtn = findViewById<Button>(R.id.closePopupButton)
        closePopupBtn.setOnClickListener {
            instructionPopup.visibility = View.GONE
            showPopupButton.visibility = View.VISIBLE
            hideBlurIfNoPopups()
        }

        // Show instruction popup again
        showPopupButton.setOnClickListener {
            hideAllPopups()
            instructionPopup.visibility = View.VISIBLE
            showPopupButton.visibility = View.GONE
            applyBlur()
        }

        // Mood button click
        chooseMoodBtn.setOnClickListener {
            hideAllPopups()
            moodPopup.visibility = View.VISIBLE
            applyBlur()
        }

        // Submit mood
        val submitBtn = findViewById<Button>(R.id.submitMoodBtn)
        submitBtn.setOnClickListener {
            moodPopup.visibility = View.GONE
            hideBlurIfNoPopups()

            val recommendationText = findViewById<TextView>(R.id.recommendationText)
            recommendationText.text = "You chose: ${selectedEmotions.joinToString(", ")}"

            selectedEmotions.clear()
            resetEmotionButtons()
        }

        // Ensure blur on first launch
        if (instructionPopup.visibility == View.VISIBLE) {
            applyBlur()
        }
    }

    private fun setupEmotionButtons() {
        emotionGrid.removeAllViews()
        for (emotion in emotions) {
            val btn = ToggleButton(this).apply {
                text = emotion
                textOn = emotion
                textOff = emotion
                isAllCaps = false
                setTextColor(ContextCompat.getColor(context, android.R.color.black))
                background = ContextCompat.getDrawable(context, android.R.drawable.btn_default)
                setPadding(16, 8, 16, 8)
                minWidth = 250
                minHeight = 150
            }

            btn.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (selectedEmotions.size < 3) {
                        selectedEmotions.add(emotion)
                        btn.setBackgroundResource(android.R.color.holo_blue_light)
                    } else {
                        btn.isChecked = false
                        Toast.makeText(this, "You can only pick 3 moods!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    selectedEmotions.remove(emotion)
                    btn.setBackgroundResource(android.R.drawable.btn_default)
                }
            }

            val layoutParams = GridLayout.LayoutParams().apply {
                width = GridLayout.LayoutParams.WRAP_CONTENT
                height = GridLayout.LayoutParams.WRAP_CONTENT
                setMargins(8, 8, 8, 8)
            }

            emotionGrid.addView(btn, layoutParams)
        }
    }

    private fun resetEmotionButtons() {
        for (i in 0 until emotionGrid.childCount) {
            val child = emotionGrid.getChildAt(i)
            if (child is ToggleButton) {
                child.isChecked = false
                child.setBackgroundResource(android.R.drawable.btn_default)
            }
        }
    }

    private fun hideAllPopups() {
        instructionPopup.visibility = View.GONE
        moodPopup.visibility = View.GONE
        showPopupButton.visibility = View.GONE
    }

    private fun applyBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurContainer.setRenderEffect(
                RenderEffect.createBlurEffect(10f, 10f, Shader.TileMode.CLAMP)
            )
        }
        dimOverlay.visibility = View.VISIBLE
    }

    private fun hideBlurIfNoPopups() {
        if (instructionPopup.visibility != View.VISIBLE && moodPopup.visibility != View.VISIBLE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                blurContainer.setRenderEffect(null)
            }
            dimOverlay.visibility = View.GONE
        }
    }
}
