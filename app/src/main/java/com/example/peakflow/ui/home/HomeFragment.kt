package com.example.peakflow.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.peakflow.R
import com.example.peakflow.data.MountainRepository
import com.example.peakflow.databinding.FragmentHomeBinding
import com.example.peakflow.domain.SortOrder
import com.example.peakflow.ui.animateClick
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(MountainRepository.getInstance(requireContext()))
    }

    private val adapter by lazy {
        MountainAdapter { mountain ->
            val bundle = Bundle().apply { putInt("mountainId", mountain.id) }
            findNavController().navigate(R.id.action_home_to_detail, bundle)
        }
    }

    private var regionsSetUp = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvMountains.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HomeFragment.adapter
        }

        binding.etSearch.doAfterTextChanged { text ->
            viewModel.setSearchQuery(text?.toString().orEmpty())
        }

        binding.chipGroupSort.setOnCheckedStateChangeListener { _, checkedIds ->
            val order = when (checkedIds.firstOrNull()) {
                R.id.chip_sort_height -> SortOrder.HEIGHT_DESC
                R.id.chip_sort_difficulty -> SortOrder.DIFFICULTY_ASC
                else -> SortOrder.DEFAULT
            }
            viewModel.setSortOrder(order)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectMountainList() }
                launch { collectNextGoal() }
                launch { collectRegions() }
            }
        }
    }

    private suspend fun collectMountainList() {
        combine(
            viewModel.filteredMountains,
            viewModel.conqueredIds,
            viewModel.userStats
        ) { mountains, conquered, stats ->
            mountains.map { MountainAdapter.MountainItem(it, it.id in conquered, stats.level) }
        }.collect { items -> adapter.submitList(items) }
    }

    private suspend fun collectNextGoal() {
        viewModel.nextGoal.collect { goal ->
            if (goal != null) {
                binding.cardNextGoal.visibility = View.VISIBLE
                binding.tvNextGoal.text = goal.name
                binding.cardNextGoal.setOnClickListener { v ->
                    v.animateClick {
                        val bundle = Bundle().apply { putInt("mountainId", goal.id) }
                        findNavController().navigate(R.id.action_home_to_detail, bundle)
                    }
                }
            } else {
                binding.cardNextGoal.visibility = View.GONE
            }
        }
    }

    private suspend fun collectRegions() {
        viewModel.regions.collect { regions ->
            if (!regionsSetUp && regions.isNotEmpty()) {
                regionsSetUp = true
                setupRegionChips(regions)
            }
        }
    }

    private fun setupRegionChips(regions: List<String>) {
        while (binding.chipGroupRegion.childCount > 1) {
            binding.chipGroupRegion.removeViewAt(1)
        }
        regions.forEach { region ->
            val chip = Chip(requireContext()).apply {
                text = region
                isCheckable = true
                tag = region
            }
            binding.chipGroupRegion.addView(chip)
        }
        binding.chipGroupRegion.setOnCheckedStateChangeListener { group, checkedIds ->
            val selected = checkedIds.firstOrNull()?.let { id -> group.findViewById<Chip>(id) }
            viewModel.setRegionFilter(selected?.tag as? String)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
