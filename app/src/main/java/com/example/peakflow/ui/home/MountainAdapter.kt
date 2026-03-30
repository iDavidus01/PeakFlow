package com.example.peakflow.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.peakflow.R
import com.example.peakflow.data.Mountain

class MountainAdapter(
    private val onItemClick: (Mountain) -> Unit
) : ListAdapter<MountainAdapter.MountainItem, MountainAdapter.ViewHolder>(DiffCallback()) {

    data class MountainItem(val mountain: Mountain, val isConquered: Boolean, val userLevel: Int)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_mountain_name)
        val height: TextView = view.findViewById(R.id.tv_mountain_height)
        val region: TextView = view.findViewById(R.id.tv_mountain_region)
        val status: ImageView = view.findViewById(R.id.iv_conquered_status)
        val recommendedLevel: TextView = view.findViewById(R.id.tv_recommended_level)
        val thumb: ImageView = view.findViewById(R.id.iv_mountain_thumb)
        val card: View = view.findViewById(R.id.card_mountain)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mountain, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val m = item.mountain
        val isLocked = item.userLevel < m.requiredLevel

        holder.name.text = m.name
        holder.height.text = "${m.height} m n.p.m."
        holder.region.text = m.region
        holder.status.visibility = if (item.isConquered) View.VISIBLE else View.GONE
        
        if (isLocked) {
            holder.recommendedLevel.visibility = View.VISIBLE
            holder.recommendedLevel.text = "⚠️ Zalecany Lvl ${m.requiredLevel}"
        } else {
            holder.recommendedLevel.visibility = View.GONE
        }
        
        holder.card.setOnClickListener { view ->
            view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction {
                    onItemClick(m)
                }
            }
        }

        holder.thumb.load(m.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.mountain_placeholder)
            error(R.drawable.mountain_placeholder)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MountainItem>() {
        override fun areItemsTheSame(a: MountainItem, b: MountainItem) = a.mountain.id == b.mountain.id
        override fun areContentsTheSame(a: MountainItem, b: MountainItem) = a == b
    }
}
