package com.example.escrowhub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.escrowhub.model.UserRole
import com.example.escrowhub.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val profile: UserProfile) : AuthState()
    data class Error(val message: String) : AuthState()
    object PasswordReset : AuthState()
    object Logout : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState
    
    private val _currentProfile = MutableStateFlow<UserProfile?>(null)
    val currentProfile: StateFlow<UserProfile?> = _currentProfile

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val user = auth.currentUser
        if (user != null) {
            fetchUserProfile(user.uid)
        }
    }

    fun register(fullName: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user!!.uid
                
                val profile = UserProfile(
                    uid = uid,
                    fullname = fullName,
                    email = email,
                    role = role
                )
                
                db.collection("users").document(uid).set(profile.toMap()).await()
                
                _currentProfile.value = profile
                _authState.value = AuthState.Success(profile)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val uid = result.user!!.uid
                
                // Fetch profile inside the same coroutine to avoid state inconsistencies
                val doc = db.collection("users").document(uid).get().await()
                val profile = UserProfile(
                    uid = uid,
                    fullname = doc.getString("fullname") ?: "",
                    email = doc.getString("email") ?: email,
                    role = doc.getString("role") ?: "User"
                )
                
                _currentProfile.value = profile
                _authState.value = AuthState.Success(profile)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Login failed")
            }
        }
    }

    fun fetchUserProfile(uid: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val doc = db.collection("users").document(uid).get().await()
                if (doc.exists()) {
                    val profile = UserProfile(
                        uid = uid,
                        fullname = doc.getString("fullname") ?: "",
                        email = doc.getString("email") ?: "",
                        role = doc.getString("role") ?: "User"
                    )
                    _currentProfile.value = profile
                    _authState.value = AuthState.Success(profile)
                } else {
                    _authState.value = AuthState.Error("User profile not found")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Failed to load profile")
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.sendPasswordResetEmail(email).await()
                _authState.value = AuthState.PasswordReset
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Reset failed")
            }
        }
    }

    fun logout() {
        auth.signOut()
        _currentProfile.value = null
        _authState.value = AuthState.Logout
    }

    fun clearState() {
        _authState.value = AuthState.Idle
    }
}
