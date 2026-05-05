package com.example.peakflow.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.peakflow.R

class AchievementsAdapter : ListAdapter<AchievementState, AchievementsAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.iv_ach_icon)
        val title: TextView = view.findViewById(R.id.tv_ach_title)
        val desc: TextView = view.findViewById(R.id.tv_ach_desc)
        val xp: TextView = view.findViewById(R.id.tv_ach_xp)
        val progress: ProgressBar = view.findViewById(R.id.progress_ach)
        val progressText: TextView = view.findViewById(R.id.tv_ach_progress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_achievement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val state = getItem(position)
        val ctx = holder.itemView.context
        val ach = state.achievement

        holder.title.text = ctx.getString(ach.titleRes)
        holder.desc.text = ctx.getString(ach.descRes)
        holder.xp.text = ctx.getString(R.string.xp_reward, ach.xpReward)

        val activeColor = ContextCompat.getColor(ctx, ach.colorRes)
        val dimColor = ContextCompat.getColor(ctx, R.color.text_secondary)

        if (state.isUnlocked) {
            holder.icon.setColorFilter(activeColor)
            holder.title.setTextColor(ContextCompat.getColor(ctx, R.color.white))
            holder.xp.setTextColor(activeColor)
        } else {
            holder.icon.setColorFilter(dimColor)
            holder.title.setTextColor(dimColor)
            holder.xp.setTextColor(dimColor)
        }

        if (state.max > 1) {
            holder.progress.visibility = View.VISIBLE
            holder.progressText.visibility = View.VISIBLE
            holder.progress.progress = ((state.current.toFloat() / state.max) * 100).toInt()
            holder.progressText.text = "${state.current} / ${state.max}"
        } else {
            holder.progress.visibility = View.GONE
            holder.progressText.visibility = View.GONE
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AchievementState>() {
        override fun areItemsTheSame(a: AchievementState, b: AchievementState) =
            a.achievement::class == b.achievement::class
        override fun areContentsTheSame(a: AchievementState, b: AchievementState) = a == b
    }
}
