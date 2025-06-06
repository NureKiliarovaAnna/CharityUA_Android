package com.example.charityua_android

import android.content.Intent
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
        val progressBar: ProgressBar = view.findViewById(R.id.fundraiser_progress_bar)
        val percentText: TextView = view.findViewById(R.id.fundraiser_percent_text)
        val almostFinishedFlag: TextView = view.findViewById(R.id.fundraiser_almost_finished_flag)
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
        holder.deadline.text = fundraiser.created_at.take(10)
        holder.raised.text = fundraiser.current_amount.toString()
        holder.goal.text = fundraiser.goal_amount.toString()

        val imageUrl = fundraiser.media_urls.firstOrNull()
        if (imageUrl != null) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.placeholder)
        }

        // Прогрес у відсотках
        val progressPercent = ((fundraiser.current_amount * 100) / fundraiser.goal_amount).toInt().coerceAtMost(100)
        holder.progressBar.progress = progressPercent
        holder.percentText.text = "Прогрес: $progressPercent%"

        // Якщо залишилось менше 10% до цілі — показуємо прапорець
        val left = fundraiser.goal_amount - fundraiser.current_amount
        val percentageLeft = left.toDouble() / fundraiser.goal_amount

        if (percentageLeft < 0.1 && progressPercent < 100) {
            holder.almostFinishedFlag.visibility = View.VISIBLE
            holder.summarySection.setBackgroundColor(Color.parseColor("#C8FAD0"))
        } else {
            holder.almostFinishedFlag.visibility = View.GONE
            holder.summarySection.setBackgroundColor(Color.parseColor("#F1F1F1"))
        }

        // Завжди показуємо "ДЕТАЛЬНІШЕ" (не змінюємо колір/текст кнопки!)
        holder.button.setBackgroundColor(Color.parseColor("#002D85"))
        holder.button.text = "ДЕТАЛЬНІШЕ"

        // Відкриття деталей збору
        holder.button.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, FundraiserDetailActivity::class.java)
            intent.putExtra("fundraiser_id", fundraiser.fundraiser_id)
            context.startActivity(intent)
        }
    }
}