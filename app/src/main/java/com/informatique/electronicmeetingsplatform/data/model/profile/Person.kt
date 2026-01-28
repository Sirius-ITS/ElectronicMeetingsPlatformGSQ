package com.informatique.electronicmeetingsplatform.data.model.profile

import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val address: String? = null,
    val addresses: List<String>? = null,
    val buildingName: String? = null,
    val city: String? = null,
    val country: String? = null,
    val createdAt: String,
    val dateOfBirth: String? = null,
    val degreeId: Int? = null,
    val degreeName: String? = null,
    val email: String? = null,
    val englishFirstName: String? = null,
    val englishSecondName: String? = null,
    val extraEmail: String? = null,
    val floorNumber: String? = null,
    val fullName: String,
    val gender: String? = null,
    val genderId: Int? = null,
    val genderName: String? = null,
    val id: Int,
    val jobNumber: String? = null,
    val jobTitle: String? = null,
    val kidsCount: Int? = null,
    val maritalStatusId: Int? = null,
    val maritalStatusName: String? = null,
    val militaryNo: String? = null,
    val motherTongue: String? = null,
    val namePrefix: String? = null,
    val nationalId: String? = null,
    val nationality: String? = null,
    val organizationStartDate: String? = null,
    val organizationEndDate: String? = null,
    val personDepartmentJobs: List<PersonDepartmentJob>,
    val personTypeId: Int? = null,
    val personTypeName: Int? = null,
    val personalPhotoPath: String,
    val phoneNumber: String? = null,
    val postalCode: String? = null,
    val qualificationTitle: String? = null,
    val rankId: Int? = null,
    val rankName: String? = null,
    val religion: String? = null,
    val rfidAccessCardCode: String? = null,
    val seniorityNo: String? = null,
    val smsPhoneNumber: String? = null,
    val streetAddress: String? = null,
    val supplyUnitId: Int? = null,
    val supplyUnitName: String? = null,
    val telephones: List<Telephone>,
    val unitId: Int? = null,
    val unitName: String? = null,
    val updatedAt: String,
    val workPhone: String? = null
){
    val formattedDateOfBirth: String
        get() = try {
            dateOfBirth?.let {
                val instant = java.time.Instant.parse(it)
                val localDate = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy").format(localDate)
            } ?: ""
        } catch (_: Exception) {
            dateOfBirth ?: ""
        }
}