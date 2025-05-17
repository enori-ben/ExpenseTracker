@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.expensetracker.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.expensetracker.model.InvoiceResult
import com.example.expensetracker.repository.Routes
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: String,
    viewModel: TransactionViewModel,
    navController: NavController
) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val transaction = transactions.find { it.id == transactionId }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                actions = {
                    if (transaction != null) {
                        IconButton(
                            onClick = { navController.navigate("${Routes.ADD_TRANSACTION}?editId=${transaction.id}") }
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit Transaction")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete Transaction")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Color(0xFF2E343B))
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xEB16292F))
        ) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                if (transaction != null) {
                    if (transaction.invoiceDetails == null) {
                        ManualTransactionDetails(transaction)
                    } else {
                        InvoiceTransactionDetails(transaction)
                    }
                } else {
                    Text(
                        text = "Transaction not found",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.deleteTransaction(transactionId)
                            showDeleteDialog = false
                            navController.popBackStack()
                        }) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("Confirm Deletion") },
                    text = { Text("Are you sure you want to delete this transaction?") }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ManualTransactionDetails(transaction: Transaction) {
    DetailItem("Amount", "%,.2f DZD".format(transaction.amount))
    DetailItem("Category", transaction.category)
    DetailItem(
        "Type",
        if (transaction.isExpense) "Expense" else "Income",
        color = if (transaction.isExpense) Color.Red else Color.Green
    )
    DetailItem("Date", transaction.date.format(DateTimeFormatter.ISO_LOCAL_DATE))

    Spacer(modifier = Modifier.height(16.dp))
    transaction.manualDescription.takeIf { it.isNotEmpty() }?.let {
        SectionTitle("Description")
        Text(
            text = it,
            modifier = Modifier.padding(vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun InvoiceTransactionDetails(transaction: Transaction) {
    transaction.invoiceDetails?.let { invoice: InvoiceResult ->
        Column(modifier = Modifier.fillMaxSize()) {

            SectionTitle("Vendor Info")
            DetailItem("Vendor Name", invoice.vendor.name)
            DetailItem("Address", invoice.vendor.address)

            Divider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp)

            SectionTitle("Invoice Info")
            DetailItem("Invoice Date", invoice.date)
            DetailItem("Total Amount", "%,.2f DZD".format(invoice.total))
            DetailItem("Category", invoice.type)
            DetailItem(
                "Type",
                if (transaction.isExpense) "Expense" else "Income",
                color = if (transaction.isExpense) Color.Red else Color.Green
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp)

            SectionTitle("Purchased Items")
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(invoice.items) { item ->
                    InvoiceItemRow(item)
                }
            }
        }
    }
}




@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun DetailItem(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}

@Composable
private fun InvoiceItemRow(item: com.example.expensetracker.model.InvoiceItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${item.name}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "%,.2f DZD".format(item.totalPrice),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Unit Price: %,.2f".format(item.price))
                Text("Quantity: ${item.quantity}")
            }
        }
    }
}
