package com.example.expensetracker.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.expensetracker.model.InvoiceResult
import com.example.expensetracker.view.scan.tryParseDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val isExpense: Boolean,
    val category: String,
    val date: LocalDate,
    val vendorName: String = "",
    val manualDescription: String = "",
    val invoiceDetails: InvoiceResult? = null
)

class TransactionViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _transactions = MutableStateFlow(
        savedStateHandle.get<List<Transaction>>("transactions") ?: emptyList()
    )
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _totalBalance = MutableStateFlow(
        savedStateHandle.get<Double>("balance") ?: 0.0
    )
    val totalBalance: StateFlow<Double> = _totalBalance

    private val _totalIncome = MutableStateFlow(
        savedStateHandle.get<Double>("income") ?: 0.0
    )
    val totalIncome: StateFlow<Double> = _totalIncome

    private val _totalExpense = MutableStateFlow(
        savedStateHandle.get<Double>("expense") ?: 0.0
    )
    val totalExpense: StateFlow<Double> = _totalExpense

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTransaction(
        amount: Double,
        isExpense: Boolean,
        category: String,
        date: LocalDate,
        vendorName: String = "",
        manualDescription: String = "",
        invoiceDetails: InvoiceResult? = null
    ) {
        if (amount <= 0) return

        // تأكد من حفظ التاريخ بتنسيق موحد (ISO)
        val standardizedDate = date

        val tx = Transaction(
            amount = amount,
            isExpense = isExpense,
            category = if (isExpense) category else "Income",
            date = standardizedDate,
            vendorName = vendorName,
            manualDescription = manualDescription,
            invoiceDetails = invoiceDetails
        )

        _transactions.update { it + tx }
        recalculateTotals()
        saveAllStates()
    }

    fun updateTransaction(
        transactionId: String,
        newAmount: Double,
        newIsExpense: Boolean,
        newCategory: String,
        newDate: LocalDate
    ) {
        _transactions.update { transactions ->
            transactions.map { transaction ->
                if (transaction.id == transactionId) {
                    val oldAmount = transaction.amount
                    val wasExpense = transaction.isExpense

                    // Update totals
                    if (wasExpense != newIsExpense) {
                        if (wasExpense) {
                            _totalExpense.value -= oldAmount
                        } else {
                            _totalIncome.value -= oldAmount
                        }
                    } else {
                        val diff = oldAmount - (if (wasExpense) newAmount else -newAmount)
                        if (wasExpense) {
                            _totalExpense.value -= diff
                        } else {
                            _totalIncome.value -= diff
                        }
                    }

                    transaction.copy(
                        amount = newAmount,
                        isExpense = newIsExpense,
                        category = newCategory,
                        date = newDate
                    )
                } else {
                    transaction
                }
            }
        }

        recalculateTotals()
        saveAllStates()
    }

    fun deleteTransaction(transactionId: String) {
        _transactions.value.find { it.id == transactionId }?.let {
            _transactions.update { list -> list.filterNot { t -> t.id == transactionId } }
            recalculateTotals()
            saveAllStates()
        }
    }


    private fun recalculateTotals() {
        var income = 0.0
        var expense = 0.0

        _transactions.value.forEach { transaction ->
            if (transaction.isExpense) {
                expense += transaction.amount
            } else {
                income += transaction.amount
            }
        }

        _totalIncome.value = income
        _totalExpense.value = expense
        _totalBalance.value = income - expense
    }

    private fun saveAllStates() {
        savedStateHandle["transactions"] = _transactions.value
        savedStateHandle["balance"]      = _totalBalance.value
        savedStateHandle["income"]       = _totalIncome.value
        savedStateHandle["expense"]      = _totalExpense.value
    }
}















//    @RequiresApi(Build.VERSION_CODES.O)
//    fun searchTransactionsByDate(searchDate: String): List<Transaction> {
//        val queryDate = tryParseDate(searchDate)
//        return _transactions.value.filter { it.date == queryDate }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun searchTransactionsByPeriod(startDateStr: String, endDateStr: String): List<Transaction> {
//        val startDate = tryParseDate(startDateStr)
//        val endDate = tryParseDate(endDateStr)
//        return _transactions.value.filter {
//            it.date in startDate..endDate
//        }
//    }