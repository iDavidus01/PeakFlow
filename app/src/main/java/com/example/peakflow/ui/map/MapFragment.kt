package com.example.peakflow.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.peakflow.R
import com.example.peakflow.data.MountainRepository
import com.example.peakflow.databinding.FragmentMapBinding
import com.example.peakflow.ui.difficultyColorRes
import org.osmdroid.config.Configuration
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMap()
        loadMountains()
    }

    private fun setupMap() {
        binding.mapView.apply {
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            isHorizontalMapRepetitionEnabled = false
            isVerticalMapRepetitionEnabled = false
            setScrollableAreaLimitDouble(BoundingBox(85.0, 180.0, -85.0, -180.0))
            minZoomLevel = 2.0
            maxZoomLevel = 15.0
            controller.setZoom(2.5)
            controller.setCenter(GeoPoint(30.0, 10.0))
        }
    }

    private fun loadMountains() {
        val repo = MountainRepository.getInstance(requireContext())
        repo.mountains.value
            .filter { it.lat != 0.0 || it.lng != 0.0 }
            .forEach { m ->
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_mountains)
                    ?.mutate()
                    ?.also { d ->
                        DrawableCompat.setTint(d, ContextCompat.getColor(requireContext(), m.difficultyColorRes()))
                    } ?: return@forEach

                Marker(binding.mapView).apply {
                    position = GeoPoint(m.lat, m.lng)
                    title = m.name
                    icon = drawable
                    setOnMarkerClickListener { _, _ ->
                        val bundle = Bundle().apply { putInt("mountainId", m.id) }
                        findNavController().navigate(R.id.action_map_to_detail, bundle)
                        true
                    }
                    binding.mapView.overlays.add(this)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDetach()
        _binding = null
    }
}
