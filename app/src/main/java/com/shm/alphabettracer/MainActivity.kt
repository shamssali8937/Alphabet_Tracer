package com.shm.alphabettracer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView
    private val letters = ('A'..'Z').toList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler = findViewById(R.id.lettersGrid)
        recycler.layoutManager = GridLayoutManager(this, 3)
        recycler.adapter = GridLetterAdapter(letters) { letter ->
            val i = Intent(this, LetterActivity::class.java)
            i.putExtra("letter", letter.toString())
            startActivity(i)
        }

        findViewById<android.view.View>(R.id.btnMatch).setOnClickListener {
            startActivity(Intent(this, MatchActivity::class.java))
        }
    }


}
