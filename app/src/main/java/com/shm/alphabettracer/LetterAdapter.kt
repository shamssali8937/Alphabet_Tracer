package com.shm.alphabettracer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LetterAdapter(
    private val letters: List<Char>,
    private val onClick: (Char) -> Unit
) : RecyclerView.Adapter<LetterAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val letterText: TextView = view.findViewById(R.id.letterText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_letter, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ch = letters[position]
        holder.letterText.text = ch.toString()
        holder.itemView.setOnClickListener { onClick(ch) }
    }

    override fun getItemCount() = letters.size
}
