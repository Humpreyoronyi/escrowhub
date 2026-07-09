package com.example.escrowhub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.escrowhub.model.Wallet
import com.example.escrowhub.model.WalletTransaction
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WalletViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    
    // Where the balance info will be held
    private val _wallet = MutableStateFlow<Wallet?>(null)
    val wallet: StateFlow<Wallet?> = _wallet
    
    // Where the list of Transactions will be held
    private val _transactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val transactions: StateFlow<List<WalletTransaction>> = _transactions
            
    // Methods
    fun fetchWallet(userId: String) {
        viewModelScope.launch {
            try {
                val doc = db.collection("wallets").document(userId).get().await()
                if (doc.exists()) {
                    _wallet.value = doc.toObject(Wallet::class.java)
                } else {
                    // If no wallet exists yet, create an empty one
                    val newWallet = Wallet(userId = userId, availableBalance = 0.0, lockedBalance = 0.0)
                    db.collection("wallets").document(userId).set(newWallet).await()
                    _wallet.value = newWallet
                }
            } catch (e: Exception) {
                // Error handling is done here
            }
        }
    }

    fun fetchTransactions(userId: String) {
        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { value, error -> 
                if (error != null) return@addSnapshotListener
                
                val list = value?.documents?.mapNotNull { doc ->
                    doc.toObject(WalletTransaction::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                
                _transactions.value = list.sortedByDescending { it.timestamp }
            }
    }
}
