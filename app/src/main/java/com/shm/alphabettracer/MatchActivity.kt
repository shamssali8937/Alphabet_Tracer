package com.shm.alphabettracer

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class MatchActivity : AppCompatActivity() {

    private val letters = ('A'..'Z').toList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)
        MusicManager.init(this)

        // Start playing music
        MusicManager.play()
        nextRound()
    }

    private fun nextRound() {
        val tvLetter = findViewById<TextView>(R.id.matchLetter)
        val ivPic = findViewById<ImageView>(R.id.matchImage)
        val b1 = findViewById<Button>(R.id.choice1)
        val b2 = findViewById<Button>(R.id.choice2)
        val b3 = findViewById<Button>(R.id.choice3)

        val correct = letters.random()
        tvLetter.text = correct.toString()
        val correctUrl = SampleData.imageFor(correct)
        Glide.with(this).load(correctUrl).into(ivPic)

        // choose two wrong options
        val wrongs = letters.filter { it != correct }.shuffled().take(2)
        val choices = (listOf(correct) + wrongs).shuffled()

        b1.text = choices[0].toString()
        b2.text = choices[1].toString()
        b3.text = choices[2].toString()

        // Remove previous click listeners to avoid multiple triggers
        b1.setOnClickListener(null)
        b2.setOnClickListener(null)
        b3.setOnClickListener(null)

        val handler = { ch: String ->
            if (ch[0] == correct) {
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
                // only go to next round if correct
                nextRound()
            } else {
                Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show()
                // do NOT go to next round
            }
        }

        b1.setOnClickListener { handler(b1.text.toString()) }
        b2.setOnClickListener { handler(b2.text.toString()) }
        b3.setOnClickListener { handler(b3.text.toString()) }
    }
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
        // Release music resources when app is destroyed
        MusicManager.release()
    }

}
