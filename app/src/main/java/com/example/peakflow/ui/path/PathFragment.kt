package com.example.peakflow.ui.path

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.peakflow.R
import com.example.peakflow.data.MountainRepository
import com.example.peakflow.databinding.FragmentPathBinding

class PathFragment : Fragment() {

    private var _binding: FragmentPathBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PathViewModel
    private lateinit var adapter: PathAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPathBinding.inflate(inflater, container, false)

        val repo = MountainRepository.getInstance(requireContext())
        viewModel = ViewModelProvider(this, PathViewModelFactory(repo))[PathViewModel::class.java]

        adapter = PathAdapter { mountain ->
            val bundle = Bundle().apply { putInt("mountainId", mountain.id) }
            findNavController().navigate(R.id.action_path_to_detail, bundle)
        }

        binding.rvPath.layoutManager = LinearLayoutManager(context)
        binding.rvPath.adapter = adapter

        viewModel.sortedMountains.observe(viewLifecycleOwner) { updateList() }
        viewModel.conqueredIds.observe(viewLifecycleOwner) { updateList() }
        viewModel.nextSuggestedIndex.observe(viewLifecycleOwner) { updateList() }

        return binding.root
    }

    private fun updateList() {
        val mountains = viewModel.sortedMountains.value.orEmpty()
        val conquered = viewModel.conqueredIds.value.orEmpty()
        val nextIdx = viewModel.nextSuggestedIndex.value ?: -1

        val items = mountains.mapIndexed { index, mountain ->
            PathAdapter.PathItem(
                mountain = mountain,
                isConquered = mountain.id in conquered,
                isNextSuggested = index == nextIdx,
                stepNumber = index + 1,
                totalDifficulty = mountain.condReq + mountain.techReq + mountain.acclReq + mountain.riskReq
            )
        }
        adapter.submitList(items)

        // Auto-scroll to suggested mountain
        if (nextIdx >= 0) {
            binding.rvPath.post {
                val scrollPos = maxOf(0, nextIdx - 1)
                (binding.rvPath.layoutManager as? LinearLayoutManager)
                    ?.scrollToPositionWithOffset(scrollPos, 0)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
