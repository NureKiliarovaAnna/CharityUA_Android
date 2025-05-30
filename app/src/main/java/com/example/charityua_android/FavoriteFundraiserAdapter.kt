package com.example.charityua_android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FavoriteFundraiserAdapter(
    private val favorites: List<FavoriteFundraiser>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<FavoriteFundraiserAdapter.FavoriteFundraiserViewHolder>() {

    inner class FavoriteFundraiserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.fundraiser_title)
        val image: ImageView = itemView.findViewById(R.id.fundraiser_image)
        val raised: TextView = itemView.findViewById(R.id.fundraiser_raised)
        val goal: TextView = itemView.findViewById(R.id.fundraiser_goal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteFundraiserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fundraiser_card, parent, false)
        return FavoriteFundraiserViewHolder(view)
    }

    override fun getItemCount(): Int = favorites.size

    override fun onBindViewHolder(holder: FavoriteFundraiserViewHolder, position: Int) {
        val fundraiser = favorites[position]

        holder.title.text = fundraiser.title
        holder.raised.text = fundraiser.current_amount.toString()
        holder.goal.text = fundraiser.goal_amount.toString()

        val imageUrl = fundraiser.cover_image_url
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.placeholder)
        }

        holder.itemView.setOnClickListener {
            onItemClick(fundraiser.fundraiser_id)
        }

        val detailButton = holder.itemView.findViewById<Button>(R.id.fundraiser_button)
        detailButton.setOnClickListener {
            onItemClick(fundraiser.fundraiser_id)
        }
    }
}