package com.mesutkarahan.smarttravel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mesutkarahan.smarttravel.model.Plan
import com.mesutkarahan.smarttravel.service.TravelRepository
import com.mesutkarahan.smarttravel.model.TravelInfoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TravelViewModel(application: Application, private val repository: TravelRepository) : AndroidViewModel(application) {
    val foodUploading = MutableLiveData<Boolean>()
    private val _planList = MutableLiveData<List<Plan>>()
    val planList: LiveData<List<Plan>>
        get() = _planList

    private val db = FirebaseFirestore.getInstance()


    fun getTravelInfo() = liveData(Dispatchers.IO) {
        val retrievedInfo = repository.getTravelInfo()
        emit(retrievedInfo)
    }
    fun getFireStoreData() {
        repository.getFireStoreData { plans ->
            _planList.value = plans
        }
    }

    fun insertTravelInfo(travelInfo: TravelInfoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTravelInfo(travelInfo)
        }
    }

    // Harita verilerini çekmek için fonksiyon
    fun getPlansForMap(): LiveData<List<Plan>> {
        return repository.getPlansForMap()
    }
    fun addPlan(plan: Plan) {
        viewModelScope.launch {
            val planMap = mapOf(
                "location" to plan.location,
                "description" to plan.description,
                "latitude" to plan.latitude,
                "longitude" to plan.longitude
            )
            db.collection("travelPlans").add(planMap)
        }
    }

    fun updatePlan(id: String, updatedPlan: Plan) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePlan(id, updatedPlan)
        }
    }

    fun deletePlan(planId: String) {
        repository.deletePlan(planId)
    }

    fun addPlanToFirestore(travelInfo: TravelInfoEntity) {
        repository.addPlanToFirestore(travelInfo)
    }
}