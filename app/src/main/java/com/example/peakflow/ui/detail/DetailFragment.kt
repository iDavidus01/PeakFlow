package com.example.peakflow.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.peakflow.R
import com.example.peakflow.data.Mountain
import com.example.peakflow.data.MountainRepository
import com.example.peakflow.data.UserStats
import com.example.peakflow.databinding.FragmentDetailBinding
import com.example.peakflow.domain.ReadinessLevel
import com.example.peakflow.ui.heightDisplay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels {
        DetailViewModelFactory(MountainRepository.getInstance(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mountainId = requireArguments().getInt("mountainId")
        viewModel.loadMountain(mountainId)

        binding.btnConquer.setOnClickListener { viewModel.toggleConquered() }
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectMountain() }
                launch { collectConquered() }
                launch { collectWeather() }
                launch { collectReadiness() }
            }
        }
    }

    private suspend fun collectMountain() {
        viewModel.mountain.collect { m ->
            m ?: return@collect
            with(binding) {
                tvDetailName.text = m.name
                tvDetailHeight.text = m.heightDisplay
                tvDetailRegion.text = m.region
                tvDetailDescription.text = m.description
                tvCondValue.text = m.condReq.toString()
                tvTechValue.text = m.techReq.toString()
                tvAcclValue.text = m.acclReq.toString()
                tvRiskValue.text = m.riskReq.toString()
                progressCond.progress = m.condReq
                progressTech.progress = m.techReq
                progressAccl.progress = m.acclReq
                progressRisk.progress = m.riskReq
                ivDetailImage.load(m.imageUrl) {
                    crossfade(300)
                    placeholder(R.drawable.mountain_placeholder)
                    error(R.drawable.mountain_placeholder)
                }
            }
        }
    }

    private suspend fun collectConquered() {
        viewModel.isConquered.collect { conquered ->
            with(binding.btnConquer) {
                if (conquered) {
                    text = getString(R.string.conquered)
                    setIconResource(R.drawable.ic_check_circle)
                    setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.conquered_green))
                } else {
                    text = getString(R.string.mark_conquered)
                    setIconResource(R.drawable.ic_flag)
                    setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accent_orange))
                }
            }
        }
    }

    private suspend fun collectWeather() {
        viewModel.weather.collect { state ->
            if (state != null) {
                binding.cardWeather.visibility = View.VISIBLE
                with(binding) {
                    tvWeatherTemp.text = "${state.temp}°C"
                    tvWeatherWind.text = "${state.wind} km/h"
                    tvWeatherStatus.text = getString(state.condition.labelRes)
                    tvWeatherStatus.setTextColor(ContextCompat.getColor(requireContext(), state.condition.colorRes))
                    tvWeatherDesc.text = getString(state.condition.descRes) + when {
                        state.isSnowing -> getString(R.string.weather_snowing_suffix)
                        state.isSunny -> getString(R.string.weather_sunny_suffix)
                        else -> ""
                    }
                }
            } else {
                binding.cardWeather.visibility = View.GONE
            }
        }
    }

    private suspend fun collectReadiness() {
        combine(viewModel.mountain, viewModel.userStats) { m, stats -> m to stats }
            .collect { (m, stats) -> m?.let { updateReadiness(it, stats) } }
    }

    private fun updateReadiness(m: Mountain, stats: UserStats) {
        val missingPts = maxOf(0, m.condReq - stats.condition) +
                maxOf(0, m.techReq - stats.technique) +
                maxOf(0, m.acclReq - stats.acclimatization) +
                maxOf(0, m.riskReq - stats.risk)
        val score = maxOf(0, 100 - missingPts * 15)
        val level = ReadinessLevel.from(score)
        binding.tvReadinessPercent.text = "$score%"
        binding.tvReadinessStatus.text = getString(level.labelRes)
        binding.tvReadinessStatus.setTextColor(ContextCompat.getColor(requireContext(), level.colorRes))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
