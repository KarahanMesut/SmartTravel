package com.mesutkarahan.smarttravel.service

import com.mesutkarahan.smarttravel.model.Plan

interface PlanClickListener {
    fun onPlanClick(plan: Plan)
}