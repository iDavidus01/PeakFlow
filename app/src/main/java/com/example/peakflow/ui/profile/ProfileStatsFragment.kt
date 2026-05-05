package com.example.peakflow.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.peakflow.R
import com.example.peakflow.data.Mountain
import com.example.peakflow.data.MountainRepository
import com.example.peakflow.databinding.FragmentProfileStatsBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileStatsFragment : Fragment() {

    private var _binding: FragmentProfileStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { ProfileViewModelFactory(MountainRepository.getInstance(requireContext())) }
    )

    private val dateFormat by lazy { SimpleDateFormat("d MMM yyyy", Locale.getDefault()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.userStats.collect { stats ->
                    with(binding) {
                        tvCondValue.text = "${stats.condition}/5"
                        tvTechValue.text = "${stats.technique}/5"
                        tvAcclValue.text = "${stats.acclimatization}/5"
                        tvRiskValue.text = "${stats.risk}/5"
                        tvTotalXp.text = stats.totalXp.toString()
                        tvProfileLevel.text = stats.level.toString()
                        tvProfileXpProgress.text = "${stats.totalXp} / ${stats.maxLevelXp}"
                        progressLevel.progress = (stats.progressToNextLevel * 100).toInt()
                        radarChart.setStats(stats.condition, stats.technique, stats.acclimatization, stats.risk)
                    }
                }}
                launch { viewModel.conqueredCount.collect { binding.tvConqueredCount.text = it.toString() } }
                launch { viewModel.totalMountains.collect { binding.tvTotalMountains.text = it.toString() } }
                launch { viewModel.totalHeightClimbed.collect { binding.tvTotalHeight.text = "$it m" } }
                launch { viewModel.regionsConquered.collect { binding.tvRegionsCount.text = it.toString() } }
                launch { viewModel.highestConquered.collect { binding.tvHighestPeak.text = it?.name ?: getString(R.string.stat_none) } }
                launch { viewModel.hardestConquered.collect { binding.tvHardestPeak.text = it?.name ?: getString(R.string.stat_none) } }
                launch { viewModel.conqueredHistory.collect { updateLogbook(it) } }
            }
        }
    }

    private fun updateLogbook(history: List<Pair<Mountain, Long>>) {
        val container = binding.logbookContainer
        val emptyView = binding.tvLogbookEmpty

        val existingEntries = (0 until container.childCount)
            .map { container.getChildAt(it) }
            .filter { it.id != R.id.tv_logbook_empty }
        existingEntries.forEach { container.removeView(it) }

        if (history.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            return
        }

        emptyView.visibility = View.GONE
        history.take(10).forEach { (mountain, timestamp) ->
            val dateStr = if (timestamp == 0L) getString(R.string.logbook_date_unknown)
                          else dateFormat.format(Date(timestamp))
            val row = LayoutInflater.from(requireContext()).inflate(R.layout.item_logbook_entry, container, false)
            row.findViewById<TextView>(R.id.tv_logbook_name).text = mountain.name
            row.findViewById<TextView>(R.id.tv_logbook_date).text = dateStr
            row.findViewById<TextView>(R.id.tv_logbook_height).text = "${mountain.height} m"
            container.addView(row)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
