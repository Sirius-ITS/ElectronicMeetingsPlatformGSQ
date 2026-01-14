package com.informatique.electronicmeetingsplatform.data.model.profile

import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val address: String? = null,
    val addresses: List<String>? = null,
    val city: String,
    val country: String,
    val createdAt: String,
    val dateOfBirth: String,
    val degreeId: Int? = null,
    val degreeName: String? = null,
    val email: String,
    val englishFirstName: String,
    val englishSecondName: String,
    val fullName: String,
    val gender: String? = null,
    val genderId: Int? = null,
    val genderName: String? = null,
    val id: Int,
    val jobNumber: String? = null,
    val jobTitle: String,
    val kidsCount: Int,
    val maritalStatusId: Int? = null,
    val maritalStatusName: String? = null,
    val militaryNo: String? = null,
    val motherTongue: String,
    val namePrefix: String,
    val nationalId: String? = null,
    val nationality: String,
    val personDepartmentJobs: List<PersonDepartmentJob>,
    val personTypeId: Int? = null,
    val personTypeName: Int? = null,
    val personalPhotoPath: String,
    val phoneNumber: String,
    val postalCode: String,
    val qualificationTitle: String? = null,
    val rankId: Int? = null,
    val rankName: String? = null,
    val religion: String,
    val rfidAccessCardCode: String? = null,
    val seniorityNo: String? = null,
    val smsPhoneNumber: String,
    val streetAddress: String,
    val supplyUnitId: Int? = null,
    val supplyUnitName: String? = null,
    val telephones: List<Telephone>,
    val unitId: Int? = null,
    val unitName: String? = null,
    val updatedAt: String,
    val workPhone: String
){
    val formattedDateOfBirth: String
        get() = try {
            val instant = java.time.Instant.parse(dateOfBirth)
            val localDate = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy").format(localDate)
        } catch (_: Exception) {
            dateOfBirth
        }
}