package com.shm.alphabettracer

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GridLetterAdapter(
    private val letters: List<Char>,
    private val onClick: (Char) -> Unit
) : RecyclerView.Adapter<GridLetterAdapter.VH>() {

    private val bgColors = listOf("#FFAB91", "#FFCC80", "#C5E1A5", "#B39DDB", "#90CAF9", "#FFE082")

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val letterText: TextView = view.findViewById(R.id.letterText)
        val bgImage: ImageView = view.findViewById(R.id.bgImage)
        val card: CardView = view as CardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_letter_grid, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ch = letters[position]
        holder.letterText.text = ch.toString()

        // set background tint from color list
        holder.card.setCardBackgroundColor(Color.parseColor(bgColors[position % bgColors.size]))

        // load a sample image for each letter (unsplash / local resource)
        val url = SampleData.imageFor(ch)

        Glide.with(holder.itemView.context)
            .load(url)
            .centerCrop()
            .into(holder.bgImage)

        holder.itemView.setOnClickListener { onClick(ch) }
    }

    override fun getItemCount() = letters.size
}
