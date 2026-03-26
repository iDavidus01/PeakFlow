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

        binding.rvMountains.layoutManager = LinearLayoutManager(context)
        binding.rvMountains.adapter = adapter

        binding.etSearch.doAfterTextChanged {
            viewModel.setSearchQuery(it.toString())
        }

        viewModel.filteredMountains.observe(viewLifecycleOwner) { mountains ->
            val conquered = viewModel.conqueredIds.value.orEmpty()
            adapter.submitList(mountains.map {
                MountainAdapter.MountainItem(it, it.id in conquered)
            })
        }

        viewModel.conqueredIds.observe(viewLifecycleOwner) { conquered ->
            val mountains = viewModel.filteredMountains.value.orEmpty()
            adapter.submitList(mountains.map {
                MountainAdapter.MountainItem(it, it.id in conquered)
            })
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}