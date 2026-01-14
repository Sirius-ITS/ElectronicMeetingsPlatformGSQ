package com.informatique.electronicmeetingsplatform.data.model.profile

import kotlinx.serialization.Serializable

@Serializable
data class PersonDepartmentJob(
    val createdAt: String,
    val departmentId: Int,
    val departmentJobId: Int,
    val departmentJobSectors: List<String>? = null,
    val departmentName: String,
    val endDate: String? = null,
    val id: Int,
    val isActive: Boolean,
    val jobId: Int,
    val jobTitle: String,
    val personDegree: String? = null,
    val personId: Int,
    val personName: String,
    val personRank: String? = null,
    val personType: String? = null,
    val personalPhotoPath: String? = null,
    val sectorId: Int? = null,
    val sectorName: String? = null,
    val startDate: String
)