package com.example.peakflow.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.peakflow.R
import com.example.peakflow.data.MountainRepository
import com.example.peakflow.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        val repo = MountainRepository.getInstance(requireContext())
        viewModel = ViewModelProvider(this, DetailViewModelFactory(repo))[DetailViewModel::class.java]

        val mountainId = arguments?.getInt("mountainId") ?: return binding.root
        viewModel.loadMountain(mountainId)

        viewModel.mountain.observe(viewLifecycleOwner) { m ->
            binding.tvDetailName.text = m.name
            binding.tvDetailHeight.text = "${m.height} m n.p.m."
            binding.tvDetailRegion.text = m.region
            binding.tvDetailDescription.text = m.description

            binding.tvCondValue.text = m.condReq.toString()
            binding.tvTechValue.text = m.techReq.toString()
            binding.tvAcclValue.text = m.acclReq.toString()
            binding.tvRiskValue.text = m.riskReq.toString()

            binding.progressCond.progress = m.condReq
            binding.progressTech.progress = m.techReq
            binding.progressAccl.progress = m.acclReq
            binding.progressRisk.progress = m.riskReq
        }

        viewModel.isConquered.observe(viewLifecycleOwner) { conquered ->
            if (conquered) {
                binding.btnConquer.text = getString(R.string.conquered)
                binding.btnConquer.setIconResource(R.drawable.ic_check_circle)
                binding.btnConquer.setBackgroundColor(
                    resources.getColor(R.color.conquered_green, null)
                )
            } else {
                binding.btnConquer.text = getString(R.string.mark_conquered)
                binding.btnConquer.setIconResource(R.drawable.ic_flag)
                binding.btnConquer.setBackgroundColor(
                    resources.getColor(R.color.accent_orange, null)
                )
            }
        }

        binding.btnConquer.setOnClickListener {
            viewModel.toggleConquered()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
