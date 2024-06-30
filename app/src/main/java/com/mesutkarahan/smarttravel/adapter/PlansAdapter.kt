package com.mesutkarahan.smarttravel.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mesutkarahan.smarttravel.databinding.ItemPlanBinding
import com.mesutkarahan.smarttravel.model.Plan
import com.mesutkarahan.smarttravel.model.TravelInfoEntity
import com.mesutkarahan.smarttravel.service.PlanClickListener


class PlansAdapter(
    val planList: ArrayList<Plan>,
    private val planClickListener: PlanClickListener
) : RecyclerView.Adapter<PlansAdapter.PlanHolder>() {


    class PlanHolder(val binding: ItemPlanBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanHolder {
        val binding = ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanHolder, position: Int) {
        val plan = planList[position]
        holder.binding.plan = plan
        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            planClickListener.onPlanClick(plan)
        }

        Log.d("PlansAdapter", "onBindViewHolder: Position $position, Location: ${plan.location}, Description: ${plan.description}")
    }

    override fun getItemCount(): Int {
        Log.d("PlansAdapter", "Item count: ${planList.size}")
        return planList.size
    }

    fun updatePlanList(newPlanList: List<Plan>) {
        planList.clear()
        planList.addAll(newPlanList)
        notifyDataSetChanged()
    }
    interface PlanClickListener {
        fun onPlanClick(plan: Plan)
    }
}
