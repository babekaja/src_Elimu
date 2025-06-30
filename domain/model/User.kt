package com.kotlingdgocucb.elimuApp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int = 0,
    val name: String,
    val email: String,
    @SerialName("is_logged_in") val isLoggedIn: Boolean,
    @SerialName("profile_picture_uri") val profilePictureUri: String,
    val track: String,
    val level: String = "",
    @SerialName("mentor_name") val mentorName: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("mentor_email") val mentorEmail: String = "",
    @SerialName("mentor_profile_url") val mentorProfileUrl: String = "",
    @SerialName("mentor_experience") val mentorExperience: String = "",
    @SerialName("mentor_description") val mentorDescription: String = "",
    @SerialName("mentor_github_url") val mentorGithubUrl: String = "",
    @SerialName("mentor_linkedin_url") val mentorLinkedinUrl: String = "",
    @SerialName("mentor_x_url") val mentorXUrl: String = "",
    @SerialName("mentor_instagram_url") val mentorInstagramUrl: String = ""
) {
    companion object {
        fun createEmpty() = User(
            name = "",
            email = "",
            isLoggedIn = false,
            profilePictureUri = "",
            track = "",
            mentorName = "",
            createdAt = ""
        )
    }
    
    val isComplete: Boolean
        get() = name.isNotBlank() && 
                email.isNotBlank() && 
                track.isNotBlank() && 
                mentorName.isNotBlank()
}