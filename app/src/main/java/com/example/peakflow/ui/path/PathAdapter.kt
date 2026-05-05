package com.example.peakflow.ui.path

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.peakflow.R
import com.example.peakflow.data.Mountain
import com.example.peakflow.ui.animateClick

class PathAdapter(
    private val onItemClick: (Mountain) -> Unit
) : ListAdapter<PathAdapter.PathItem, PathAdapter.ViewHolder>(DiffCallback()) {

    data class PathItem(
        val mountain: Mountain,
        val isConquered: Boolean,
        val isNextSuggested: Boolean,
        val stepNumber: Int,
        val totalDifficulty: Int
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stepNumber: TextView = view.findViewById(R.id.tv_step_number)
        val stepLine: View = view.findViewById(R.id.view_step_line)
        val stepDot: View = view.findViewById(R.id.view_step_dot)
        val name: TextView = view.findViewById(R.id.tv_path_name)
        val height: TextView = view.findViewById(R.id.tv_path_height)
        val difficulty: TextView = view.findViewById(R.id.tv_path_difficulty)
        val status: ImageView = view.findViewById(R.id.iv_path_status)
        val card: View = view.findViewById(R.id.card_path)
        val suggestedBadge: TextView = view.findViewById(R.id.tv_suggested_badge)
        val thumb: ImageView = view.findViewById(R.id.iv_path_thumb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_path_mountain, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val ctx = holder.itemView.context

        holder.stepNumber.text = item.stepNumber.toString()
        holder.name.text = item.mountain.name
        holder.height.text = "${item.mountain.height} m"
        holder.difficulty.text = "Trudność: ${item.totalDifficulty}/20"

        holder.thumb.load(item.mountain.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.mountain_placeholder)
            error(R.drawable.mountain_placeholder)
        }

        holder.stepLine.visibility = if (position < itemCount - 1) View.VISIBLE else View.INVISIBLE

        when {
            item.isConquered -> {
                holder.status.visibility = View.VISIBLE
                holder.status.setImageResource(R.drawable.ic_check_circle)
                holder.status.setColorFilter(ContextCompat.getColor(ctx, R.color.conquered_green))
                holder.stepDot.background.setTint(ContextCompat.getColor(ctx, R.color.conquered_green))
                holder.stepNumber.setTextColor(ContextCompat.getColor(ctx, R.color.conquered_green))
                holder.suggestedBadge.visibility = View.GONE
            }
            item.isNextSuggested -> {
                holder.status.visibility = View.GONE
                holder.stepDot.background.setTint(ContextCompat.getColor(ctx, R.color.accent_orange))
                holder.stepNumber.setTextColor(ContextCompat.getColor(ctx, R.color.accent_orange))
                holder.suggestedBadge.visibility = View.VISIBLE
            }
            else -> {
                holder.status.visibility = View.GONE
                holder.stepDot.background.setTint(ContextCompat.getColor(ctx, R.color.text_secondary))
                holder.stepNumber.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary))
                holder.suggestedBadge.visibility = View.GONE
            }
        }

        holder.card.setOnClickListener { v -> v.animateClick { onItemClick(item.mountain) } }
    }

    class DiffCallback : DiffUtil.ItemCallback<PathItem>() {
        override fun areItemsTheSame(a: PathItem, b: PathItem) = a.mountain.id == b.mountain.id
        override fun areContentsTheSame(a: PathItem, b: PathItem) = a == b
    }
}
