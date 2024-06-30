package com.mesutkarahan.smarttravel.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.mesutkarahan.smarttravel.R
import com.mesutkarahan.smarttravel.databinding.FragmentAddPlanBinding
import com.mesutkarahan.smarttravel.databinding.FragmentRegisterBinding
import com.mesutkarahan.smarttravel.model.Plan
import com.mesutkarahan.smarttravel.model.TravelInfoEntity
import com.mesutkarahan.smarttravel.roomdb.TravelInfoDatabase
import com.mesutkarahan.smarttravel.service.TravelApiService
import com.mesutkarahan.smarttravel.service.TravelRepository
import com.mesutkarahan.smarttravel.viewmodel.TravelViewModel
import com.mesutkarahan.smarttravel.viewmodel.TravelViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AddPlanFragment : Fragment() {
    private var _binding: FragmentAddPlanBinding? = null
    private val binding get() = _binding!!
    private lateinit var travelViewModel: TravelViewModel
    private val args: AddPlanFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddPlanBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveButton.setOnClickListener {savePlan(it)}
        val args: AddPlanFragmentArgs by navArgs()
        if (args.latitude != 0f && args.longitude != 0f) {
            binding.latitudeEditText.setText(args.latitude.toString())
            binding.longitudeEditText.setText(args.longitude.toString())
        }

        binding.selectLocationButton.setOnClickListener {
            val emptyPlan = Plan("", "", "", 0.0,0.0,Date()) // Boş bir Plan nesnesi oluşturun
            val action = AddPlanFragmentDirections.actionAddPlanFragmentToMapFragment(
                originFragment = "AddPlanFragment",
                plan = emptyPlan
            )
            Navigation.findNavController(it).navigate(action)
        }

        binding.selectDateButton.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(parentFragmentManager, "date_picker")

            datePicker.addOnPositiveButtonClickListener {
                val selectedDate = it
                binding.dateEditText.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate)))
            }
        }

        val application = requireNotNull(this.activity).application
        val apiService = TravelApiService.create()
        val firestore = FirebaseFirestore.getInstance()
        val travelInfoDatabase = TravelInfoDatabase.getDatabase(application)
        val repository = TravelRepository(apiService, firestore, travelInfoDatabase)
        val factory = TravelViewModelFactory(application, repository)
        travelViewModel = ViewModelProvider(this, factory).get(TravelViewModel::class.java)
    }

    private fun savePlan(view: View) {
        val location = binding.locationEditText.text.toString().trim()
        val description = binding.descriptionEditText.text.toString().trim()
        val latitudeText = binding.latitudeEditText.text.toString().trim()
        val longitudeText = binding.longitudeEditText.text.toString().trim()
        val dateText = binding.dateEditText.text.toString().trim()

        if (location.isNotEmpty() && description.isNotEmpty() && latitudeText.isNotEmpty() && longitudeText.isNotEmpty() && dateText.isNotEmpty()) {
            val latitude = latitudeText.toDoubleOrNull() ?: 0.0
            val longitude = longitudeText.toDoubleOrNull() ?: 0.0
            val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateText)?.time ?: 0L

            val travelInfo = TravelInfoEntity(
                id = UUID.randomUUID().toString(),
                location = location,
                description = description,
                date = Date(),
                latitude = latitude,
                longitude = longitude,
                travelDate = selectedDate
            )

            travelViewModel.insertTravelInfo(travelInfo)
            Toast.makeText(context, "Plan saved!", Toast.LENGTH_SHORT).show()
            val action = AddPlanFragmentDirections.actionAddPlanFragmentToViewPlansFragment()
            Navigation.findNavController(view).navigate(action)
        } else {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}