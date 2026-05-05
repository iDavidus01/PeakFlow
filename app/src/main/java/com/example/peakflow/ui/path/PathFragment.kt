package com.example.peakflow.ui.path

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.peakflow.R
import com.example.peakflow.data.MountainRepository
import com.example.peakflow.databinding.FragmentPathBinding
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class PathFragment : Fragment() {

    private var _binding: FragmentPathBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PathViewModel by viewModels {
        PathViewModelFactory(MountainRepository.getInstance(requireContext()))
    }

    private val adapter by lazy {
        PathAdapter { mountain ->
            val bundle = Bundle().apply { putInt("mountainId", mountain.id) }
            findNavController().navigate(R.id.action_path_to_detail, bundle)
        }
    }

    private var scrollTarget: Int by Delegates.observable(-1) { _, old, new ->
        if (new != old && new >= 0) {
            binding.rvPath.post {
                (binding.rvPath.layoutManager as? LinearLayoutManager)
                    ?.scrollToPositionWithOffset(maxOf(0, new - 1), 0)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPathBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPath.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@PathFragment.adapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectPathItems() }
                launch {
                    viewModel.nextSuggestedIndex.collect { idx -> scrollTarget = idx }
                }
            }
        }
    }

    private suspend fun collectPathItems() {
        combine(
            viewModel.sortedMountains,
            viewModel.conqueredIds,
            viewModel.nextSuggestedIndex
        ) { mountains, conquered, nextIdx ->
            mountains.mapIndexed { index, mountain ->
                PathAdapter.PathItem(
                    mountain = mountain,
                    isConquered = mountain.id in conquered,
                    isNextSuggested = index == nextIdx,
                    stepNumber = index + 1,
                    totalDifficulty = mountain.totalDifficulty
                )
            }
        }.collect { items -> adapter.submitList(items) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
