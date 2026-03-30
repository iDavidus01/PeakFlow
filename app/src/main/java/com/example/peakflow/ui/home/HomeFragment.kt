package com.example.peakflow.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.peakflow.R
import com.example.peakflow.data.MountainRepository
import com.example.peakflow.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: MountainAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val repo = MountainRepository.getInstance(requireContext())
        viewModel = ViewModelProvider(this, HomeViewModelFactory(repo))[HomeViewModel::class.java]

        adapter = MountainAdapter { mountain ->
            val bundle = Bundle().apply { putInt("mountainId", mountain.id) }
            findNavController().navigate(R.id.action_home_to_detail, bundle)
        }
        
        // Remove problematic line if it's not strictly necessary or causes issues
        // adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.rvMountains.layoutManager = LinearLayoutManager(context)
        binding.rvMountains.adapter = adapter

        binding.etSearch.doAfterTextChanged {
            viewModel.setSearchQuery(it.toString())
        }

        viewModel.filteredMountains.observe(viewLifecycleOwner) {
            updateList()
        }

        viewModel.conqueredIds.observe(viewLifecycleOwner) {
            updateList()
        }

        viewModel.userStats.observe(viewLifecycleOwner) {
            updateList()
        }

        viewModel.nextGoal.observe(viewLifecycleOwner) { goal ->
            if (goal != null) {
                binding.cardNextGoal.visibility = View.VISIBLE
                binding.tvNextGoal.text = goal.name
                binding.cardNextGoal.setOnClickListener { view ->
                    view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
                        view.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction {
                            val bundle = Bundle().apply { putInt("mountainId", goal.id) }
                            findNavController().navigate(R.id.action_home_to_detail, bundle)
                        }
                    }
                }
            } else {
                binding.cardNextGoal.visibility = View.GONE
            }
        }

        return binding.root
    }

    private fun updateList() {
        val mountains = viewModel.filteredMountains.value.orEmpty()
        val conquered = viewModel.conqueredIds.value.orEmpty()
        val userLevel = viewModel.userStats.value?.level ?: 1
        adapter.submitList(mountains.map {
            MountainAdapter.MountainItem(it, it.id in conquered, userLevel)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
