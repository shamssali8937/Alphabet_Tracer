package com.shm.alphabettracer

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.util.*

class LetterActivity : AppCompatActivity() {
    private lateinit var tracingView: TracingView
    private lateinit var tts: TextToSpeech
    private var letter = 'A'
    private lateinit var titleTv: TextView

    private val prefs by lazy { getSharedPreferences("abckids", Context.MODE_PRIVATE) }
    private val allLetters = ('A'..'Z').toList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letter)
        MusicManager.init(this)

        // Start playing music
        MusicManager.play()

        titleTv = findViewById(R.id.letterTitle)
        tracingView = findViewById(R.id.tracingView)
        val btnClear: Button = findViewById(R.id.btnClear)
        val btnHint: Button = findViewById(R.id.btnHint)

        letter = (intent.getStringExtra("letter") ?: "A")[0]
        tracingView.post {
            showLetter(letter)
        }

        tracingView.onTraceSuccess = {
            prefs.edit().putBoolean("done_${letter}", true).apply()
            runOnUiThread {
                speakPhonic()
                Toast.makeText(this, "Great! You traced $letter", Toast.LENGTH_SHORT).show()
     //           showExampleForLetter(letter)
                // auto next after short delay
                tracingView.postDelayed({
                    goToNextLetter()
                }, 900)
            }
        }

        btnClear.setOnClickListener { tracingView.clearStrokes() }
        btnHint.setOnClickListener { tracingView.hint() }

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
            }
        }
    }

    private fun showLetter(c: Char) {
        letter = c
        titleTv.text = "Trace: $letter"
        tracingView.clearStrokes()
        tracingView.setLetter(letter)
    }

    private fun goToNextLetter() {
        val idx = allLetters.indexOf(letter)
        if (idx >= 0 && idx < allLetters.size - 1) {
            val next = allLetters[idx + 1]
            showLetter(next)
        } else {
            // finished all letters
            Toast.makeText(this, "You completed all letters!", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun speakPhonic() {
        val word = SampleData.wordFor(letter)
        val toSay = "$letter. $word."
        tts.speak(toSay, TextToSpeech.QUEUE_FLUSH, null, "ID_${letter}")
    }

//    private fun showExampleForLetter(c: Char) {
//        val imageUrl = SampleData.imageFor(c)
//        val iv = android.widget.ImageView(this)
//        Glide.with(this).load(imageUrl).into(iv)
//        val d = androidx.appcompat.app.AlertDialog.Builder(this)
//            .setTitle("$c for ${SampleData.wordFor(c).replaceFirstChar { it.titlecase(Locale.getDefault()) }}")
//            .setView(iv)
//            .setPositiveButton("OK", null)
//            .create()
//        d.show()
//    }
override fun onPause() {
    super.onPause()
    // Pause music when app goes to background
    MusicManager.pause()
}

    override fun onResume() {
        super.onResume()
        // Resume music when app comes to foreground
        if (!MusicManager.isPlaying()) {
            MusicManager.play()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
        MusicManager.release()
    }
}
