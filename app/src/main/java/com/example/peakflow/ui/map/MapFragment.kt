package com.example.peakflow.ui.map

import android.graphics.Color
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
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMap()
        loadMountains()
    }

    private fun setupMap() {
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        
        binding.mapView.isHorizontalMapRepetitionEnabled = false
        binding.mapView.isVerticalMapRepetitionEnabled = false
        binding.mapView.setScrollableAreaLimitDouble(org.osmdroid.util.BoundingBox(85.0, 180.0, -85.0, -180.0))
        binding.mapView.minZoomLevel = 2.0
        binding.mapView.maxZoomLevel = 15.0
        
        val mapController = binding.mapView.controller
        mapController.setZoom(2.5)
        mapController.setCenter(GeoPoint(30.0, 10.0))
    }

    private fun loadMountains() {
        val repo = MountainRepository.getInstance(requireContext())
        val mountains = repo.mountains.value ?: emptyList()

        for (m in mountains) {
            if (m.lat == 0.0 && m.lng == 0.0) continue

            val marker = Marker(binding.mapView)
            marker.position = GeoPoint(m.lat, m.lng)
            marker.title = m.name
            
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_mountains)!!.mutate()
            val color = when {
                m.totalDifficulty < 10 -> Color.parseColor("#4CAF50")
                m.totalDifficulty in 10..14 -> Color.parseColor("#FFC107")
                m.totalDifficulty in 15..18 -> Color.parseColor("#FF9800")
                else -> Color.parseColor("#F44336")
            }
            DrawableCompat.setTint(drawable, color)
            
            marker.icon = drawable

            marker.setOnMarkerClickListener { _, _ ->
                val bundle = Bundle().apply { putInt("mountainId", m.id) }
                findNavController().navigate(R.id.action_map_to_detail, bundle)
                true
            }

            binding.mapView.overlays.add(marker)
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
