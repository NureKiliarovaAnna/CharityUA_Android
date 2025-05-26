package com.example.charityua_android

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FundraiserAdapter(
    private val fundraisers: List<Fundraiser>,
    private val onDetailsClick: (Fundraiser) -> Unit
) : RecyclerView.Adapter<FundraiserAdapter.FundraiserViewHolder>() {

    inner class FundraiserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.fundraiser_title)
        val image: ImageView = view.findViewById(R.id.fundraiser_image)
        val deadline: TextView = view.findViewById(R.id.fundraiser_deadline)
        val raised: TextView = view.findViewById(R.id.fundraiser_raised)
        val goal: TextView = view.findViewById(R.id.fundraiser_goal)
        val button: Button = view.findViewById(R.id.fundraiser_button)
        val summarySection: LinearLayout = view.findViewById(R.id.summary_section)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FundraiserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fundraiser_card, parent, false)
        return FundraiserViewHolder(view)
    }

    override fun getItemCount(): Int = fundraisers.size

    override fun onBindViewHolder(holder: FundraiserViewHolder, position: Int) {
        val fundraiser = fundraisers[position]

        holder.title.text = fundraiser.title
        holder.deadline.text = fundraiser.created_at.take(10) // YYYY-MM-DD
        holder.raised.text = fundraiser.current_amount.toString()
        holder.goal.text = fundraiser.goal_amount.toString()

        // Завантаження зображення (перше з media_urls)
        val imageUrl = fundraiser.media_urls.firstOrNull()
        if (imageUrl != null) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(holder.image)
        }

        // Обчислюємо відсоток до завершення
        val left = fundraiser.goal_amount - fundraiser.current_amount
        val percentageLeft = left.toDouble() / fundraiser.goal_amount

        if (percentageLeft < 0.1) {
            // < 10% до завершення
            holder.summarySection.setBackgroundColor(Color.parseColor("#C8FAD0"))
            holder.button.setBackgroundColor(Color.parseColor("#B00020"))
            holder.button.text = "ЗАВЕРШУЄТЬСЯ"
        } else {
            // Стандартний вигляд
            holder.summarySection.setBackgroundColor(Color.parseColor("#F1F1F1"))
            holder.button.setBackgroundColor(Color.parseColor("#002D85"))
            holder.button.text = "ДЕТАЛЬНІШЕ"
        }

        // Клік по кнопці
        holder.button.setOnClickListener {
            onDetailsClick(fundraiser)
        }
    }
}