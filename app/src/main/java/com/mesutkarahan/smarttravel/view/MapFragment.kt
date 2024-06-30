package com.mesutkarahan.smarttravel.view

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.mesutkarahan.smarttravel.R
import com.mesutkarahan.smarttravel.databinding.FragmentMapBinding
import com.mesutkarahan.smarttravel.roomdb.TravelInfoDatabase
import com.mesutkarahan.smarttravel.service.TravelApiService
import com.mesutkarahan.smarttravel.service.TravelRepository
import com.mesutkarahan.smarttravel.viewmodel.TravelViewModel
import com.mesutkarahan.smarttravel.viewmodel.TravelViewModelFactory

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var travelViewModel: TravelViewModel
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val args: MapFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val apiService = TravelApiService.create()
        val firestore = FirebaseFirestore.getInstance()
        val travelInfoDatabase = TravelInfoDatabase.getDatabase(application)

        val repository = TravelRepository(apiService, firestore, travelInfoDatabase)
        val factory = TravelViewModelFactory(application, repository)
        travelViewModel = ViewModelProvider(this, factory).get(TravelViewModel::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(currentLatLng).title("Mevcut Konum"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        }
        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

            when (args.originFragment) {
                "AddPlanFragment" -> {
                    val action = MapFragmentDirections.actionMapFragmentToAddPlanFragment(
                        latLng.latitude.toFloat(),
                        latLng.longitude.toFloat(),
                        args.originFragment
                    )
                    Navigation.findNavController(requireView()).navigate(action)
                }
                "PlanDetailFragment" -> {
                    args.plan?.let { plan ->
                        val action = MapFragmentDirections.actionMapFragmentToPlanDetailFragment(
                            latLng.latitude.toFloat(),
                            latLng.longitude.toFloat(),
                            args.originFragment,
                            plan
                        )
                        Navigation.findNavController(requireView()).navigate(action)
                    }
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
