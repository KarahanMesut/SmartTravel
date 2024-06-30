package com.mesutkarahan.smarttravel.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.mesutkarahan.smarttravel.model.Plan
import com.mesutkarahan.smarttravel.model.TravelInfoEntity
import com.mesutkarahan.smarttravel.roomdb.TravelInfoDatabase
import com.mesutkarahan.smarttravel.service.TravelApiService
import com.mesutkarahan.smarttravel.service.TravelInfoDto
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Query
import java.util.Date

class TravelRepository(private val apiService: TravelApiService,    private val firestore: FirebaseFirestore,
                       private val travelInfoDatabase: TravelInfoDatabase) {

    suspend fun getTravelInfo(): List<TravelInfoEntity> {
        val snapshot = firestore.collection("travelPlans").get().await()
        val travelPlans = snapshot.documents.map { document ->
            document.toObject(TravelInfoEntity::class.java)!!.apply {
                // Eğer id alanı int olacaksa, burada dönüştürmeyi yapabilirsiniz
                // id = document.id.toInt() // Bu kodu kaldırmanız gerekebilir
            }
        }
        val localTravelPlans = travelInfoDatabase.travelInfoDao().getAllTravelInfo()

        return travelPlans + localTravelPlans
    }

    fun getFireStoreData(callback: (List<Plan>) -> Unit) {
        firestore.collection("travelPlans").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error != null) {
                Log.d("TravelRepository", "getFireStoreData Error: ${error.localizedMessage}")
                callback(emptyList())
            } else {
                val planList = mutableListOf<Plan>()
                if (value != null && !value.isEmpty) {
                    val documents = value.documents
                    for (document in documents) {
                        val id = document.id
                        val location = document.getString("location") ?: ""
                        val description = document.getString("description") ?: ""
                        val latitude = document.getDouble("latitude") ?: 0.0
                        val longitude = document.getDouble("longitude") ?: 0.0
                        val date = document.getDate("date") ?: Date()
                        val travelDate = document.getLong("travelDate") ?: 0L

                        val plan = Plan(id, location, description, latitude, longitude, date,travelDate)
                        planList.add(plan)
                    }
                }
                callback(planList)
            }
        }
    }

    fun getPlansForMap(): LiveData<List<Plan>> {
        val plansLiveData = MutableLiveData<List<Plan>>()
        val db = FirebaseFirestore.getInstance()
        db.collection("travelPlans").get().addOnSuccessListener { result ->
            val plans = result.documents.mapNotNull { it.toObject(Plan::class.java) }
            plansLiveData.value = plans
        }
        return plansLiveData
    }



    suspend fun insertTravelInfo(travelInfo: TravelInfoEntity) {
        firestore.collection("travelPlans").add(travelInfo).await()
        travelInfoDatabase.travelInfoDao().insert(listOf(travelInfo))
    }

    fun addPlanToFirestore(travelInfo: TravelInfoEntity) {
        val plan = hashMapOf(
            "id" to travelInfo.id,
            "location" to travelInfo.location,
            "description" to travelInfo.description,
            "imageUrl" to travelInfo.imageUrl,
            "date" to travelInfo.date,
            "latitude" to travelInfo.latitude,
            "longitude" to travelInfo.longitude,
            "travelDate" to travelInfo.travelDate
        )

        firestore.collection("travelPlans").add(plan)
            .addOnSuccessListener { documentReference ->
                Log.d("TravelRepository", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("TravelRepository", "Error adding document", e)
            }
    }

    fun updatePlan(planId: String, updatedPlan: Plan) {
        val planMap = hashMapOf(
            "location" to updatedPlan.location,
            "description" to updatedPlan.description,
            "latitude" to updatedPlan.latitude,
            "longitude" to updatedPlan.longitude,
            "date" to updatedPlan.date,
            "travelDate" to updatedPlan.travelDate// Date alanını da ekleyin
        )

        firestore.collection("travelPlans").document(planId)
            .set(planMap)
            .addOnSuccessListener {
                Log.d("TravelRepository", "Plan successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("TravelRepository", "Error updating plan", e)
            }
    }

    fun deletePlan(planId: String) {
        firestore.collection("travelPlans").document(planId)
            .delete()
            .addOnSuccessListener {
                Log.d("TravelRepository", "Plan successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w("TravelRepository", "Error deleting plan", e)
            }
    }





}