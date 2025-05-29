package com.example.charityua_android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class DonationAdapter(
    private val onClick: (Int) -> Unit
) : ListAdapter<DonationWithFundraiser, DonationAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.fundraiserTitle)
        private val amount: TextView = view.findViewById(R.id.donationAmount)
        private val date: TextView = view.findViewById(R.id.donationDate)

        fun bind(item: DonationWithFundraiser) {
            title.text = item.fundraiser.title
            amount.text = "${item.amount} грн"
            date.text = item.created_at.substring(0, 10)

            itemView.setOnClickListener {
                onClick(item.fundraiser.fundraiser_id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_donation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<DonationWithFundraiser>() {
        override fun areItemsTheSame(oldItem: DonationWithFundraiser, newItem: DonationWithFundraiser): Boolean {
            return oldItem.donation_id == newItem.donation_id
        }

        override fun areContentsTheSame(oldItem: DonationWithFundraiser, newItem: DonationWithFundraiser): Boolean {
            return oldItem == newItem
        }
    }
}