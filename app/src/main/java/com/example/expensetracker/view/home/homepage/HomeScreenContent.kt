package com.example.expensetracker.view.home.homepage

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.repository.Routes
import com.example.expensetracker.view.Transaction
import com.example.expensetracker.view.TransactionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
 fun HomeScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: TransactionViewModel,
    scope: CoroutineScope,
    drawerState: DrawerState
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xEB16292F))
    ) {
        val isCompact = maxWidth < 360.dp
        // State holders
        val transactions by viewModel.transactions.collectAsStateWithLifecycle()
        val totalBalance by viewModel.totalBalance.collectAsStateWithLifecycle()
        val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
        val totalExpense by viewModel.totalExpense.collectAsStateWithLifecycle()

        var selectedPeriod by remember { mutableStateOf("Day") }
        var currentDate by remember { mutableStateOf(LocalDate.now()) }

        val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy") }
        val dailyFormatter = remember { DateTimeFormatter.ofPattern("EEEE, d MMM") }

        val filteredTransactions = remember(transactions, selectedPeriod, currentDate) {
            when (selectedPeriod) {
                "Day" -> transactions.filter { it.date == currentDate }
                "Week" -> transactions.filter {
                    val start = currentDate.with(java.time.DayOfWeek.MONDAY)
                    val end = start.plusDays(6)
                    it.date in start..end
                }
                "Month" -> transactions.filter {
                    it.date.year == currentDate.year && it.date.month == currentDate.month
                }
                "Year" -> transactions.filter { it.date.year == currentDate.year }
                else -> transactions
            }
        }

        val listState = rememberLazyListState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isCompact) 10.dp else 16.dp)
        ) {
            HeaderSection(scope, drawerState, navController)
            BalanceSummarySection(totalBalance, totalIncome, totalExpense)

            PeriodSelector(
                selectedPeriod = selectedPeriod,
                currentDate = currentDate,
                dateFormatter = dateFormatter,
                dailyFormatter = dailyFormatter,
                onPeriodSelected = { selectedPeriod = it },
                onDateChanged = { currentDate = it }
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()

                    .padding(vertical = if (isCompact) 4.dp else 16.dp)
            ) {
                if (filteredTransactions.isEmpty()) {
                    item {
                        Text(
                            text = "No transactions found",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            )
                        )
                    }
                } else {
                    items(filteredTransactions.reversed(), key = { it.id }) { tx ->
                        TransactionItem(transaction = tx) {
                            navController.navigate("${Routes.TRANSACTION_DETAIL}/${tx.id}")
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            FAB(navController)
        }
    }
}


@Composable
private fun HeaderSection(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavController
) {
    Spacer(modifier = Modifier.height(6.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { scope.launch { drawerState.open() } }) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Menu",
                tint = Color.White,
                modifier = Modifier.size(40.dp) // ← غير الحجم هنا كما تريد

            )
        }

//        IconButton(onClick = { navController.navigate(Routes.PROFILE_SCREEN) }) {
//            Icon(
//                painter = painterResource(R.drawable.profile),
//                contentDescription = "Profile",
//                tint = Color.White
//            )
//        }
    }
    Spacer(modifier = Modifier.height(46.dp))

}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun PeriodSelector(
    selectedPeriod: String,
    currentDate: LocalDate,
    dateFormatter: DateTimeFormatter,
    dailyFormatter: DateTimeFormatter,
    onPeriodSelected: (String) -> Unit,
    onDateChanged: (LocalDate) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Day", "Week", "Month", "Year").forEach { period ->
                    PeriodChip(
                        text = period,
                        isSelected = period == selectedPeriod,
                        onSelect = { onPeriodSelected(period) }
                    )
                }
            }

            DateNavigation(
                currentDate = currentDate,
                dateFormatter = dateFormatter,
                dailyFormatter = dailyFormatter,
                onDateChanged = onDateChanged
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DateNavigation(
    currentDate: LocalDate,
    dateFormatter: DateTimeFormatter,
    dailyFormatter: DateTimeFormatter,
    onDateChanged: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),

        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        IconButton(onClick = { onDateChanged(currentDate.minusDays(1)) }) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Previous",
                tint = Color(0xFF666666)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { showDatePicker = true } //Make the date clickable
        ) {
            Text(
                text = currentDate.format(dateFormatter),
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF838383)
            )
            Text(
                text = currentDate.format(dailyFormatter),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF7A7A7A)
            )
        }

        IconButton(onClick = { onDateChanged(currentDate.plusDays(1)) }) {
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "Next",
                tint = Color(0xFF666666)
            )
        }
    }
    IconButton(
        onClick = { showDatePicker = true },
        modifier = Modifier.size(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = "Select Date",
            tint = Color(0xFF070000)
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = Instant.ofEpochMilli(millis)
                        val newDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
                        onDateChanged(newDate)
                    }
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = { Text(text = "Select Day",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)) }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TransactionItem(transaction: Transaction, onClick: () -> Unit) {

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { onClick() }
            .border(
                width = 1.5.dp,
                color = Color(0xFF000000).copy(alpha = 0.7f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE7C182)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = transaction.date.format(dateFormatter), // تنسيق التاريخ هنا
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "${if (transaction.isExpense) "-" else "+"} %,.2f DZD".format(transaction.amount),
                color = if (transaction.isExpense) Color(0xFFF44336) else Color(0xFF4CAF50),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


@Composable
fun PeriodChip(text: String, isSelected: Boolean, onSelect: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable(onClick = onSelect)
            .drawBehind {
                if (isSelected) {
                    val strokeWidth = 2.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = Color(0xFFFFFFFF),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }
            }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .padding(bottom = if (isSelected) 4.dp else 0.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color(0xFF666666),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

@Composable
fun FAB(navController: NavController) {

    FloatingActionButton(
        onClick = {
            navController.navigate(Routes.ADD_TRANSACTION)
        },
        modifier = Modifier
            .padding(19.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                spotColor = Color.Black.copy(alpha = 0.3f)
            ),
        containerColor = Color(0xFFFFBB0C),
        shape = CircleShape,
        contentColor = Color.Black
    ) {
        Icon(
            painter = painterResource(id = R.drawable.add),
            contentDescription = "Add Transaction",
            modifier = Modifier.size(32.dp)
        )
    }


}


@Composable
private fun BalanceSummarySection(
    totalBalance: Double,
    totalIncome: Double,
    totalExpense: Double
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Total Balance",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        Text(
            text = "%,.2f DZD".format(totalBalance),
            style = MaterialTheme.typography.displaySmall.copy(
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = if (totalBalance >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 9.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IncomeExpenseItem("Income", totalIncome, Color(0xFF4CAF50))
            IncomeExpenseItem("Expense", totalExpense, Color(0xFFF44336))
        }
    }
}

@Composable
private fun IncomeExpenseItem(label: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White // ← أضف هذا السطر لتحديد اللون الأبيض
        )
        Text(
            text = "%,.2f DZD".format(amount),
            color = color,
            style = MaterialTheme.typography.titleMedium
        )
    }
}