package com.example.escrowhub.viewmodel

//viewmodel packages allow state/data management for our ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// firebase authentication /firestore product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
//import our models
import com.example.escrowhub.model.UserRole
import com.example.escrowhub.model.UserProfile
import com.google.rpc.context.AttributeContext
//coroutines for background processing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

//sealed class ;it cannot be used to create objects
// confined for usage inside our viewmodel
sealed class AuthState {
    object Idle : AuthState()//state when user is not logged in
    object Loading : AuthState()//state when user is attempting to login()// state when user attempt to login
    //when user is logged in in tag the profile
    data class Success(val profile: UserProfile) : AuthState()
    //if error occurs on login attempt
    data class Error(val message: String) : AuthState()
    //state on password reset process
    object PasswordReset : AuthState()
    //state when user logs out
    object Logout : AuthState()
}
class AuthViewModel : ViewModel() {
    // reference to firebase auth classes
    // private  means variable can only be used inside script
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState
    private val _currentProfile = MutableStateFlow<UserProfile?>(null)
    val currentProfile: StateFlow<UserProfile?> = _currentProfile

    //called on app start to check if user is logged in in
    //if so redirect to dashboard skip auth screens
    init {
        val user = auth.currentUser
        if (user != null) {
            fetchUserProfile(user.uid)
        }
    }
    fun register(fullName: String,
                 email: String,
                 password: String,
                 role: String) {
        viewModelScope.launch {
            //set auth state as loading when user hits submit
            _authState.value = AuthState.Loading
            try {
                //create firebase account for sure
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                //being a backgroung process we wait for results
                //on success we capture the user id
                val uid = result.user!!.uid
                //2. create user profile and store in firestore
                val profile = UserProfile(
                    uid = uid,
                    fullname = fullName,
                    email = email,
                    role = role
                )
                //save to firestore inside the users collection
                db.collection("users").document(uid).set(profile.toMap()).await()
                //load the current user profile for current user
                _currentProfile.value = profile
                _authState.value = AuthState.Success(profile)

            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")

            }
        }
    }
    //login
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                fetchUserProfile(result.user!!.uid)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun fetchUserProfile(uid: String) {
        //TODO : fetch user profile from firestore
        viewModelScope.launch {
            try {
                _authState.value =  AuthState.Loading
                // First we create a reference to our firestore collection
                val doc = db.collection("users")
                    .document(uid)
                    .get().await()
                // Pack our data into our model class
                val profile = UserProfile(
                    uid = uid,
                    fullname = doc.getString("fullname") ?:"",
                    email = doc.getString("email") ?:"",
                    role = doc.getString("role") ?:"Student"
                )
                // Set the date to our viewmodel observer
                _currentProfile.value = profile
                _authState.value = AuthState.Success(profile)

            } catch(e: Exception){
                _authState.value = AuthState.Error(e.message ?: "Failed To Load Profile")
            }
        }
    }
    //FORGOT PASSWORD
    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.sendPasswordResetEmail(email).await()
                _authState.value = AuthState.PasswordReset
            }catch (e: Exception){
                _authState.value = AuthState.Error(e.message ?: "Reset failed")

            }
        }

    }
    //logout
    fun logout() {
        auth.signOut()
        _currentProfile.value = null
        _authState.value = AuthState.Logout
    }
    // clear state
    fun clearState() {
        _authState.value = AuthState.Idle
    }
}