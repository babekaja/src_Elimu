package com.kotlingdgocucb.elimuApp.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.kotlingdgocucb.elimuApp.data.datasource.local.datastore.PreferenceKeys
import com.kotlingdgocucb.elimuApp.domain.model.User
import com.kotlingdgocucb.elimuApp.domain.utils.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ElimuRepositoryImpl @Inject constructor(
    private val datastore: DataStore<Preferences>,
) : ElimuRepository {

    companion object {
        private const val TAG = "ElimuRepositoryImpl"
    }

    override suspend fun setCurrentUser(user: User?): Result<User?> {
        return try {
            Log.d(TAG, "Setting current user: $user")
            datastore.edit { preferences ->
                if (user != null) {
                    preferences[PreferenceKeys.userName] = user.name
                    preferences[PreferenceKeys.userEmail] = user.email
                    preferences[PreferenceKeys.userProfilePicture] = user.profilePictureUri
                    preferences[PreferenceKeys.isLoggedIn] = user.isLoggedIn
                    preferences[PreferenceKeys.userTrack] = user.track
                    preferences[PreferenceKeys.userMentor] = user.mentorName
                } else {
                    // Clear all user data
                    preferences.clear()
                }
            }
            Log.d(TAG, "User saved successfully")
            Result.Success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user", e)
            Result.Error("Error saving user: ${e.message}")
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            Log.d(TAG, "Getting current user")
            val currentUser = datastore.data.map { preferences ->
                val isLoggedIn = preferences[PreferenceKeys.isLoggedIn] ?: false
                if (isLoggedIn) {
                    User(
                        id = 0,
                        name = preferences[PreferenceKeys.userName] ?: "",
                        email = preferences[PreferenceKeys.userEmail] ?: "",
                        profilePictureUri = preferences[PreferenceKeys.userProfilePicture] ?: "",
                        isLoggedIn = isLoggedIn,
                        track = preferences[PreferenceKeys.userTrack] ?: "",
                        mentorName = preferences[PreferenceKeys.userMentor] ?: "",
                        createdAt = "",
                        mentorEmail = "",
                        mentorProfileUrl = "",
                        mentorExperience = "",
                        mentorDescription = "",
                        mentorGithubUrl = "",
                        mentorLinkedinUrl = "",
                        mentorXUrl = "",
                        mentorInstagramUrl = ""
                    )
                } else {
                    null
                }
            }.first()

            Log.d(TAG, "Retrieved user: $currentUser")
            Result.Success(currentUser)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving user", e)
            Result.Error("Error retrieving user: ${e.message}")
        }
    }

    override suspend fun updateOrSyncCurrentUser(newUser: User?): Result<User?> {
        return try {
            Log.d(TAG, "Updating/syncing user: $newUser")
            val currentUserResult = getCurrentUser()
            val currentUser = if (currentUserResult is Result.Success) currentUserResult.data else null

            if (currentUser != newUser) {
                Log.d(TAG, "User changes detected, updating...")
                setCurrentUser(newUser)
            } else {
                Log.d(TAG, "No user changes detected")
                currentUserResult
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user", e)
            Result.Error("Error updating user: ${e.message}")
        }
    }
}