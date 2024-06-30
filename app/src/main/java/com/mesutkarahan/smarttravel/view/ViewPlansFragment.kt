package com.mesutkarahan.smarttravel.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.Query
import com.mesutkarahan.smarttravel.adapter.PlansAdapter
import com.mesutkarahan.smarttravel.databinding.FragmentViewPlansBinding
import com.mesutkarahan.smarttravel.model.Plan
import com.mesutkarahan.smarttravel.viewmodel.TravelViewModel
import com.mesutkarahan.smarttravel.model.TravelInfoEntity
import com.mesutkarahan.smarttravel.roomdb.TravelInfoDatabase
import com.mesutkarahan.smarttravel.service.TravelApiService
import com.mesutkarahan.smarttravel.service.TravelRepository
import com.mesutkarahan.smarttravel.viewmodel.TravelViewModelFactory

class ViewPlansFragment : Fragment(),PlansAdapter.PlanClickListener {

    private var _binding: FragmentViewPlansBinding? = null
    private val binding get() = _binding!!
    private lateinit var travelViewModel: TravelViewModel
    private lateinit var db: FirebaseFirestore
    val planList : ArrayList<Plan> = arrayListOf()
    private lateinit var adapter: PlansAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewPlansBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addPlanButton.setOnClickListener { addPlan(it) }
        val application = requireNotNull(this.activity).application
        val apiService = TravelApiService.create()
        val firestore = FirebaseFirestore.getInstance()
        val travelInfoDatabase = TravelInfoDatabase.getDatabase(application)
        val repository = TravelRepository(apiService, firestore, travelInfoDatabase)
        val factory = TravelViewModelFactory(application, repository)
        travelViewModel = ViewModelProvider(this, factory).get(TravelViewModel::class.java)

        adapter = PlansAdapter(planList, this)
        binding.plansRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.plansRecyclerView.adapter = adapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            travelViewModel.getFireStoreData()
        }

        travelViewModel.planList.observe(viewLifecycleOwner, { plans ->
            plans?.let {
                adapter.updatePlanList(it)
                binding.planUploading.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })

        travelViewModel.getFireStoreData()
    }

    private fun addPlan(view: View){

        val action = ViewPlansFragmentDirections.actionViewPlansFragmentToAddPlanFragment()
        Navigation.findNavController(requireView()).navigate(action)


    }

    override fun onPlanClick(plan: Plan) {
        val action = ViewPlansFragmentDirections.actionViewPlansFragmentToPlanDetailFragment(
            latitude = plan.latitude.toFloat(),
            longitude = plan.longitude.toFloat(),
            plan = plan
        )
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}