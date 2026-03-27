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
            binding.tvCondValue.text = "${stats.condition}/5"
            binding.tvTechValue.text = "${stats.technique}/5"
            binding.tvAcclValue.text = "${stats.acclimatization}/5"
            binding.tvRiskValue.text = "${stats.risk}/5"
            binding.tvTotalXp.text = stats.totalXp.toString()

            binding.radarChart.setStats(
                stats.condition,
                stats.technique,
                stats.acclimatization,
                stats.risk
            )
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
