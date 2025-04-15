package com.example.mangamatch

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

data class MangaRecommendation(val message: String, val imageResId: Int?)

class MangaMatchMainActivity : AppCompatActivity() {

    private lateinit var blurContainer: View
    private lateinit var dimOverlay: View
    private lateinit var instructionPopup: View
    private lateinit var showPopupButton: Button
    private lateinit var moodPopup: View
    private lateinit var emotionGrid: GridLayout
    private lateinit var chooseMoodBtn: Button
    private lateinit var recommendationImage: ImageView
    private val selectedEmotions = mutableListOf<String>()

    private val emotions = listOf(
        "Happy", "Sad", "Bored",
        "Depressed", "Lazy", "Hungry",
        "Lovestruck", "Scared", "Surprise Me"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mangamatchmain)

        blurContainer = findViewById(R.id.blurContainer)
        dimOverlay = findViewById(R.id.dimOverlay)
        instructionPopup = findViewById(R.id.instructionPopupWrapper)
        moodPopup = findViewById(R.id.moodPopupWrapper)
        showPopupButton = findViewById(R.id.showPopupButton)
        emotionGrid = findViewById(R.id.emotionGrid)
        chooseMoodBtn = findViewById(R.id.chooseMoodBtn)
        recommendationImage = findViewById(R.id.recommendationImage)

        setupEmotionButtons()

        val closePopupBtn = findViewById<Button>(R.id.closePopupButton)
        closePopupBtn.setOnClickListener {
            instructionPopup.visibility = View.GONE
            showPopupButton.visibility = View.VISIBLE
            hideBlurIfNoPopups()
        }

        showPopupButton.setOnClickListener {
            hideAllPopups()
            instructionPopup.visibility = View.VISIBLE
            showPopupButton.visibility = View.GONE
            applyBlur()
        }

        chooseMoodBtn.setOnClickListener {
            hideAllPopups()
            moodPopup.visibility = View.VISIBLE
            applyBlur()
        }

        val submitBtn = findViewById<Button>(R.id.submitMoodBtn)
        submitBtn.setOnClickListener {
            val finalEmotions = selectedEmotions.toMutableList()

            if (finalEmotions.contains("Surprise Me")) {
                finalEmotions.remove("Surprise Me")
                val availableEmotions = emotions.filter { it != "Surprise Me" && !finalEmotions.contains(it) }
                val needed = 2 - finalEmotions.size
                finalEmotions.addAll(availableEmotions.shuffled().take(needed))
            }

            if (finalEmotions.size != 2) {
                Toast.makeText(this, "Please select exactly 2 moods.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val recommendation = getMangaRecommendation(finalEmotions[0], finalEmotions[1])
            val recommendationText = findViewById<TextView>(R.id.recommendationText)
            recommendationText.text = recommendation.message
            recommendationText.visibility = View.VISIBLE

            if (recommendation.imageResId != null) {
                recommendationImage.setImageResource(recommendation.imageResId)
                recommendationImage.visibility = View.VISIBLE
            } else {
                recommendationImage.visibility = View.GONE
            }

            moodPopup.visibility = View.GONE
            showPopupButton.visibility = View.VISIBLE
            hideBlurIfNoPopups()

            selectedEmotions.clear()
            resetEmotionButtons()
        }

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
                    if (selectedEmotions.size < 2) {
                        selectedEmotions.add(emotion)
                        btn.setBackgroundResource(android.R.color.holo_blue_light)
                    } else {
                        btn.isChecked = false
                        Toast.makeText(this, "You can only pick 2 moods!", Toast.LENGTH_SHORT).show()
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

    private fun getMangaRecommendation(emotion1: String, emotion2: String): MangaRecommendation {
        val pair = setOf(emotion1, emotion2)

        val recommendationMap = mapOf(
            setOf("Happy", "Sad") to MangaRecommendation("We recommend you to read \"Anohana\" by Mari Okada", R.drawable.anohana),
            setOf("Happy", "Bored") to MangaRecommendation("We recommend you to read \"Sket Dance\" by Kenta Shinohara", R.drawable.sketdance),
            setOf("Happy", "Lovestruck") to MangaRecommendation("We recommend you to read \"Blue Box\" by Kouji Miura", R.drawable.bluebox),
            setOf("Happy", "Lazy") to MangaRecommendation("We recommend you to read \"Gintama\" by Hideaki Sorachi", R.drawable.gintama),
            setOf("Sad", "Depressed") to MangaRecommendation("We recommend you to read \"Orange\" by Ichigo Takano", R.drawable.orange),
            setOf("Sad", "Lovestruck") to MangaRecommendation("We recommend you to read \"Your Lie in April\" by Naoshi Arakawa", R.drawable.yourlieapril),
            setOf("Sad", "Lazy") to MangaRecommendation("We recommend you to read \"March Comes in Like a Lion\" by Chica Umino", R.drawable.march),
            setOf("Depressed", "Lazy") to MangaRecommendation("We recommend you to read \"Welcome to the NHK\" by Tatsuhiko Takimoto", R.drawable.klightnovel),
            setOf("Depressed", "Bored") to MangaRecommendation("We recommend you to read \"Solanin\" by Inio Asano", R.drawable.solanin),
            setOf("Bored", "Lazy") to MangaRecommendation("We recommend you to read \"Nichijou\" by Keiichi Arawi", R.drawable.ninchou),
            setOf("Bored", "Sad") to MangaRecommendation("We recommend you to read \"Bokurano\" by Mohiro Kitoh", R.drawable.bokurano),
            setOf("Lovestruck", "Lazy") to MangaRecommendation("We recommend you to read \"Kubo Won't Let Me Be Invisible\" by Nene Yukimori", R.drawable.kubo),
            setOf("Lovestruck", "Sad") to MangaRecommendation("We recommend you to read \"Ijiranaide, Nagatoro-san\" by Nanashi", R.drawable.nagatoro),
            setOf("Happy", "Scared") to MangaRecommendation("We recommend you to read \"Zom 100: Bucket List of the Dead\" by Haro Aso", R.drawable.zom100),
            setOf("Scared", "Sad") to MangaRecommendation("We recommend you to read \"Chainsaw Man\" by Tatsuki Fujimoto", R.drawable.chainsawman),
            setOf("Happy", "Hungry") to MangaRecommendation("We recommend you to read \"Shokugeki no Soma\" by Yūto Tsukuda", R.drawable.foodwars),
            setOf("Sad", "Hungry") to MangaRecommendation("We recommend you to read \"Isekai Izakaya Nobu\" by Natsuya Semikawa", R.drawable.nobo)
        )

        return recommendationMap[pair]
            ?: MangaRecommendation("Sorry we can't find any recommendation for that... Please mood match another type ￣へ￣", null)
    }
}
