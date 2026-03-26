package com.example.peakflow.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.peakflow.data.MountainRepository
import com.example.peakflow.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val repo = MountainRepository.getInstance(requireContext())
        viewModel = ViewModelProvider(this, ProfileViewModelFactory(repo))[ProfileViewModel::class.java]

        viewModel.userStats.observe(viewLifecycleOwner) { stats ->
            binding.tvCondValue.text = stats.condition.toString()
            binding.tvTechValue.text = stats.technique.toString()
            binding.tvAcclValue.text = stats.acclimatization.toString()
            binding.tvRiskValue.text = stats.risk.toString()
            binding.tvTotalXp.text = stats.totalXp.toString()
        }

        viewModel.conqueredCount.observe(viewLifecycleOwner) { count ->
            binding.tvConqueredCount.text = count.toString()
        }

        viewModel.totalMountains.observe(viewLifecycleOwner) { total ->
            binding.tvTotalMountains.text = total.toString()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
