package com.example.escrowhub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.escrowhub.model.Escrow
import com.example.escrowhub.network.MpesaRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// This prevents the app from running 2 processes at time
sealed class EscrowState {
    object Idle : EscrowState()   // No process is taking place
    object Loading : EscrowState()  // Trying to take data to db
    object Success : EscrowState()   // Data was successfully saved to database
    object PaymentReleased : EscrowState()
    data class SuccessList(val escrows: List<Escrow>) : EscrowState()
    data class SuccessSingle(val escrow: Escrow) : EscrowState()
    data class Error(val message: String) : EscrowState() // if there is an error
}

// Connection to Forestore for UI to look through
class EscrowViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val mpesaRepository = MpesaRepository()

    // The private "Mutable" state that cannot be changed
    private val _escrowState = MutableStateFlow<EscrowState>(EscrowState.Idle)

    // The public "Immutable" state that can be changed
    val escrowState: StateFlow<EscrowState> = _escrowState

    fun createEscrowWithPayment(escrow: Escrow, buyerPhone: String) {
        viewModelScope.launch {
            _escrowState.value = EscrowState.Loading

            // 1. Initiate M-Pesa STK Push
            val paymentResult = mpesaRepository.initiateSTKPush(
                phoneNumber = buyerPhone,
                amount = escrow.amount,
                accountRef = if (escrow.itemName.length > 12) escrow.itemName.substring(0, 12) else escrow.itemName
            )

            if (paymentResult.isSuccess) {
                try {
                    // 2. Ask Firestore for a unique ID for a new deal
                    val id = db.collection("escrows").document().id

                    // 3. Add the ID to the deal and data
                    val newEscrow = escrow.copy(id = id, status = "PENDING_PAYMENT")

                    // 4. Save the data to the "escrows" collections
                    db.collection("escrows")
                        .document(id)
                        .set(newEscrow)
                        .await()

                    _escrowState.value = EscrowState.Success
                } catch (e: Exception) {
                    _escrowState.value = EscrowState.Error("Payment prompt sent, but saving failed: ${e.message}")
                }
            } else {
                val error = paymentResult.exceptionOrNull()
                _escrowState.value = EscrowState.Error("M-Pesa Prompt Failed: ${error?.message}")
            }
        }
    }

    fun createEscrow(escrow: Escrow) {
        viewModelScope.launch {
            // UI picks up when we are busy
            _escrowState.value = EscrowState.Loading

            try {
               // 1. Ask Firestore for a unique ID for a new deal
                val id = db.collection("escrows").document().id

              // 2. Add the ID to the deal and data
                val newEscrow = escrow.copy(id = id)

              // 3. Save the data to the "escrows" collections
                db.collection("escrows")
                    .document(id)
                    .set(newEscrow)
                    .await() // Wait for the upload to finish

              // 4. Once the upload is done, it is updated to a success state!
                _escrowState.value = EscrowState.Success

            } catch (e: Exception) {
                // 5. If there was a failure Error message is updated
                _escrowState.value = EscrowState.Error(e.message ?: "Unexpected Error")
            }
        }
    }

    fun fetchUserEscrows(userEmail: String) {
        viewModelScope.launch {
            _escrowState.value = EscrowState.Loading
            db.collection("escrows")
                .whereEqualTo("buyerId", userEmail) // Buyer focused as per request
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        _escrowState.value = EscrowState.Error(error.message ?: "Fetch failed")
                        return@addSnapshotListener
                    }
                    val escrowList = value?.documents?.mapNotNull { doc ->
                        doc.toObject(Escrow::class.java)?.copy(id = doc.id)
                    } ?: emptyList()
                    _escrowState.value = EscrowState.SuccessList(escrowList)
                }
        }
    }

    fun fetchEscrowById(escrowId: String) {
        viewModelScope.launch {
            _escrowState.value = EscrowState.Loading
            try {
                val doc = db.collection("escrows").document(escrowId).get().await()
                val escrow = doc.toObject(Escrow::class.java)?.copy(id = doc.id)
                if (escrow != null) {
                    _escrowState.value = EscrowState.SuccessSingle(escrow)
                } else {
                    _escrowState.value = EscrowState.Error("Escrow not found")
                }
            } catch (e: Exception) {
                _escrowState.value = EscrowState.Error(e.message ?: "Fetch failed")
            }
        }
    }

    fun updateEscrowStatus(escrowId: String, newStatus: String) {
        viewModelScope.launch {
            _escrowState.value = EscrowState.Loading
            try {
                db.collection("escrows").document(escrowId)
                    .update("status", newStatus)
                    .await()

                // If it is a complete transaction, trigger the special state
                if (newStatus == "COMPLETED") {
                    _escrowState.value = EscrowState.PaymentReleased
                } else {
                    fetchEscrowById(escrowId)  // Refresh the detail view
                }
            } catch (e: Exception) {
                _escrowState.value = EscrowState.Error(e.message ?: "Update failed")
            }
        }
    }

    fun resetState() {
        _escrowState.value = EscrowState.Idle
    }
}
