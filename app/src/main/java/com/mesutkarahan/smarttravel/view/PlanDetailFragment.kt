package com.mesutkarahan.smarttravel.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.mesutkarahan.smarttravel.R
import com.mesutkarahan.smarttravel.databinding.FragmentPlanDetailBinding
import com.mesutkarahan.smarttravel.databinding.FragmentViewPlansBinding
import com.mesutkarahan.smarttravel.model.Plan
import com.mesutkarahan.smarttravel.roomdb.TravelInfoDatabase
import com.mesutkarahan.smarttravel.service.TravelApiService
import com.mesutkarahan.smarttravel.service.TravelRepository
import com.mesutkarahan.smarttravel.viewmodel.TravelViewModel
import com.mesutkarahan.smarttravel.viewmodel.TravelViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlanDetailFragment : Fragment() {

    private var _binding: FragmentPlanDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var travelViewModel: TravelViewModel
    private val args: PlanDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlanDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: PlanDetailFragmentArgs by navArgs()

        val application = requireNotNull(this.activity).application
        val apiService = TravelApiService.create()
        val firestore = FirebaseFirestore.getInstance()
        val travelInfoDatabase = TravelInfoDatabase.getDatabase(application)
        val repository = TravelRepository(apiService, firestore, travelInfoDatabase)
        val factory = TravelViewModelFactory(application, repository)
        travelViewModel = ViewModelProvider(this, factory).get(TravelViewModel::class.java)


        binding.selectDateButton.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(parentFragmentManager, "date_picker")

            datePicker.addOnPositiveButtonClickListener {
                val selectedDate = it
                binding.dateEditText.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate)))
            }
        }

        val plan = args.plan
        binding.locationEditText.setText(plan.location)
        binding.descriptionEditText.setText(plan.description)
        val travelDate = Date(plan.travelDate)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.dateEditText.setText(dateFormat.format(travelDate))

        if (args.latitude != 0f && args.longitude != 0f) {
            binding.latitudeEditText.setText(args.latitude.toString())
            binding.longitudeEditText.setText(args.longitude.toString())
        } else {
            binding.latitudeEditText.setText(plan.latitude.toString())
            binding.longitudeEditText.setText(plan.longitude.toString())
        }
        binding.buttonUpdate.setOnClickListener {
            val location = binding.locationEditText.text.toString().trim()
            val description = binding.descriptionEditText.text.toString().trim()
            val latitudeText = binding.latitudeEditText.text.toString().trim()
            val longitudeText = binding.longitudeEditText.text.toString().trim()
            val dateText = binding.dateEditText.text.toString().trim()

            if (location.isNotEmpty() && description.isNotEmpty() && latitudeText.isNotEmpty() && longitudeText.isNotEmpty() && dateText.isNotEmpty()) {
                val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateText)?.time ?: 0L
                val travelDate = Date(plan.travelDate)
                val latitude = latitudeText.toDoubleOrNull() ?: 0.0
                val longitude = longitudeText.toDoubleOrNull() ?: 0.0
                val alertDialog=AlertDialog.Builder(requireContext())
                    .setTitle("Update Plan")
                    .setMessage("Are you sure you want to update this plan?")
                    .setPositiveButton("Yes"){dialog, which ->
                        val updatedPlan = Plan(
                            id = plan.id,
                            location = location,
                            description = description,
                            latitude = latitude,
                            longitude = longitude,
                            date = Date(),
                            travelDate = selectedDate
                        )
                        travelViewModel.updatePlan(plan.id, updatedPlan)
                        Toast.makeText(context, "Plan updated!", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()


                    }.setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()
                    }
                    .create()

                alertDialog.show()


            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonDelete.setOnClickListener { view ->
            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("Delete Plan")
                .setMessage("Are you sure you want to delete this plan?")
                .setPositiveButton("Yes") { dialog, which ->
                    travelViewModel.deletePlan(plan.id)
                    Toast.makeText(context, "Plan deleted!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .create()

            alertDialog.show()
        }

        binding.selectLocationButton.setOnClickListener {
            val action = PlanDetailFragmentDirections.actionPlanDetailFragmentToMapFragment(
                originFragment = "PlanDetailFragment",
                plan = args.plan
            )
            Navigation.findNavController(it).navigate(action)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}