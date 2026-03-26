package com.example.peakflow.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.peakflow.R
import com.example.peakflow.data.Mountain

class MountainAdapter(
    private val onItemClick: (Mountain) -> Unit
) : ListAdapter<MountainAdapter.MountainItem, MountainAdapter.ViewHolder>(DiffCallback()) {

    data class MountainItem(val mountain: Mountain, val isConquered: Boolean)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_mountain_name)
        val height: TextView = view.findViewById(R.id.tv_mountain_height)
        val region: TextView = view.findViewById(R.id.tv_mountain_region)
        val status: ImageView = view.findViewById(R.id.iv_conquered_status)
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
        holder.name.text = m.name
        holder.height.text = "${m.height} m n.p.m."
        holder.region.text = m.region
        holder.status.visibility = if (item.isConquered) View.VISIBLE else View.GONE
        holder.card.setOnClickListener { onItemClick(m) }
    }

    class DiffCallback : DiffUtil.ItemCallback<MountainItem>() {
        override fun areItemsTheSame(a: MountainItem, b: MountainItem) = a.mountain.id == b.mountain.id
        override fun areContentsTheSame(a: MountainItem, b: MountainItem) = a == b
    }
}
