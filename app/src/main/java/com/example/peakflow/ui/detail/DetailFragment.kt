package com.example.peakflow.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
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

            // Load hero image
            binding.ivDetailImage.load(m.imageUrl) {
                crossfade(300)
                placeholder(R.drawable.mountain_placeholder)
                error(R.drawable.mountain_placeholder)
            }
            updateReadiness()
        }

        viewModel.userStats.observe(viewLifecycleOwner) {
            updateReadiness()
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

        viewModel.weather.observe(viewLifecycleOwner) { weatherState ->
            if (weatherState != null) {
                binding.cardWeather.visibility = View.VISIBLE
                binding.tvWeatherTemp.text = "${weatherState.temp}°C"
                binding.tvWeatherWind.text = "${weatherState.wind} km/h"
                binding.tvWeatherStatus.text = weatherState.conditionText
                binding.tvWeatherStatus.setTextColor(android.graphics.Color.parseColor(weatherState.conditionColor))
                binding.tvWeatherDesc.text = weatherState.desc
                
                // Extra icons based on state if desired, e.g. adding snow icon near temp
                if (weatherState.isSnowing) {
                    binding.tvWeatherDesc.text = weatherState.desc + " (Pada śnieg 🌨️)"
                } else if (weatherState.isSunny) {
                    binding.tvWeatherDesc.text = weatherState.desc + " (Czyste niebo ☀️)"
                }
            } else {
                binding.cardWeather.visibility = View.GONE
            }
        }

        binding.btnConquer.setOnClickListener {
            viewModel.toggleConquered()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun updateReadiness() {
        val m = viewModel.mountain.value ?: return
        val stats = viewModel.userStats.value ?: return

        val missingPts = maxOf(0, m.condReq - stats.condition) +
                maxOf(0, m.techReq - stats.technique) +
                maxOf(0, m.acclReq - stats.acclimatization) +
                maxOf(0, m.riskReq - stats.risk)

        // Using formula: 1 missing point = -15%
        // So 0 misses = 100%, 1 miss = 85%, 2 misses = 70%, 3 = 55%, 4 = 40%
        val readinessScore = maxOf(0, 100 - (missingPts * 15))

        binding.tvReadinessPercent.text = "$readinessScore%"
        
        when {
            readinessScore >= 80 -> {
                binding.tvReadinessStatus.text = "Gotowy"
                binding.tvReadinessStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50")) // Green
            }
            readinessScore >= 50 -> {
                binding.tvReadinessStatus.text = "Ryzykowne"
                binding.tvReadinessStatus.setTextColor(android.graphics.Color.parseColor("#FF9800")) // Orange
            }
            else -> {
                binding.tvReadinessStatus.text = "Zapomnij"
                binding.tvReadinessStatus.setTextColor(android.graphics.Color.parseColor("#F44336")) // Red
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
