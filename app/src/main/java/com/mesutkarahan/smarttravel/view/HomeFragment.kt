package com.mesutkarahan.smarttravel.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.mesutkarahan.fotografpaylasim.model.FCMHelper
import com.mesutkarahan.smarttravel.R
import com.mesutkarahan.smarttravel.databinding.FragmentHomeBinding
import com.mesutkarahan.smarttravel.databinding.FragmentLoginBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage = result.data?.data

        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        } else {
            Snackbar.make(binding.root, "İzin verilmedi", Snackbar.LENGTH_SHORT).show()
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            FCMHelper.getLastKnownLocation(requireContext())
        } else {
            Toast.makeText(requireContext(), "Konum izinleri reddedildi", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createPlanButton.setOnClickListener { createPlain(it) }
        binding.viewPlansButton.setOnClickListener { viewPlain(it) }
        binding.logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
            val action = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
            Navigation.findNavController(view).navigate(action)
        }

        FCMHelper.initialize(this, {
            Log.d("FeedFragment", "Notification permission granted.")
        }, activityResultLauncher, permissionLauncher,locationPermissionLauncher)

        FCMHelper.requestNotificationPermission(this) {
            Log.d("FeedFragment", "Notification permission granted.")
        }


    }


    private fun createPlain(view: View){
        FCMHelper.requestLocationPermission(this)

        val action = HomeFragmentDirections.actionHomeFragmentToCreatePlanFragment()
        Navigation.findNavController(view).navigate(action)

    }

    private fun viewPlain(view: View){
        FCMHelper.requestLocationPermission(this)

        val action = HomeFragmentDirections.actionHomeFragmentToViewPlansFragment()
        Navigation.findNavController(view).navigate(action)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}