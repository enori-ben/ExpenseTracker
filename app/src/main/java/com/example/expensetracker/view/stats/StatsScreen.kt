package com.example.expensetracker.view.stats

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.data.categories
import com.example.expensetracker.view.TransactionViewModel
import kotlin.math.min

@Composable
fun StatsScreen(
    navController: NavController,
    viewModel: TransactionViewModel
) {
    val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
    val totalExpense by viewModel.totalExpense.collectAsStateWithLifecycle()
    val totalBalance by viewModel.totalBalance.collectAsStateWithLifecycle()

    // مصروفات حسب فئة
    val expenseByCategory = categories
        .map { cat ->
            cat.name to viewModel.transactions.value
                .filter { it.isExpense && it.category == cat.name }
                .sumOf { it.amount }
        }
        .filter { it.second > 0.0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color( 0xFF2E343B))
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xEB16292F))

        ) {
            Box(
                modifier = Modifier
                    .padding(19.dp),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = stringResource(R.string.stats_title),
                    fontSize = 35.sp,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.4f),
                            offset = Offset(3f, 10f),
                            blurRadius = 8f
                        )
                    ),
                    modifier = Modifier.padding(start = 110.dp, top = 23.dp),
                    color = Color.White
                )


//            Text(
//                text = stringResource(R.string.stats_title),
//                style = MaterialTheme.typography.headlineLarge,
//                color = colorScheme.primary
//            )
            }
        }



        Spacer(modifier = Modifier.height(46.dp))

        Box(
            modifier = Modifier
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            PieChartSection(
                income = totalIncome,
                expenseByCategory = expenseByCategory,
                balance = totalBalance
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
//        MonthlyBarChartSection()
    }
}


@Composable
private fun PieChartSection(
    income: Double,
    expenseByCategory: List<Pair<String, Double>>,
    balance: Double
) {
    val total = income + expenseByCategory.sumOf { it.second }
    if (total <= 0.0) {
        Text(
            text = stringResource(R.string.no_data),
            color = colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
        return
    }

    val slices = mutableListOf<Pair<Float, Color>>().apply {
        add(income.toFloat() / total.toFloat() to Color(0xFF4CAF50))
        expenseByCategory.forEach { (name, amt) ->
            val c = categories.find { it.name == name }?.color ?: Color.Red
            add(amt.toFloat() / total.toFloat() to c)
        }
    }
    val labels = mutableListOf<Pair<String, Color>>().apply {
        add(stringResource(R.string.income) to Color(0xFF4CAF50))
        expenseByCategory.forEach { (name, _) ->
            val c = categories.find { it.name == name }?.color ?: Color.Red
            add(name to c)
        }
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth * 0.9f
    val targetSweep = (income.toFloat() / total.toFloat()) * 360f
    val animatedSweep by animateFloatAsState(
        targetValue = targetSweep,
        animationSpec = tween(700, easing = EaseInOutCubic)
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .width(cardWidth)
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            val chartSize = cardWidth * 0.6f
            Box(
                modifier = Modifier
                    .size(chartSize)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    var startAngle = -90f
                    val diameter = min(size.width, size.height)
                    val rect = Rect(
                        (size.width - diameter) / 2,
                        (size.height - diameter) / 2,
                        (size.width + diameter) / 2,
                        (size.height + diameter) / 2
                    )

                    drawArc(
                        color = slices[0].second,
                        startAngle = startAngle,
                        sweepAngle = animatedSweep,
                        useCenter = true,
                        topLeft = rect.topLeft,
                        size = Size(rect.width, rect.height)
                    )
                    startAngle += animatedSweep

                    for (i in 1 until slices.size) {
                        val (pct, col) = slices[i]
                        val sw = pct * 360f
                        drawArc(
                            color = col,
                            startAngle = startAngle,
                            sweepAngle = sw,
                            useCenter = true,
                            topLeft = rect.topLeft,
                            size = Size(rect.width, rect.height)
                        )
                        startAngle += sw
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.balance_title),
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorScheme.onBackground,
                        fontSize = 12.sp,

                        )
                    Text(
                        text = String.format("%,.2f", balance),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 180.dp)
            ) {
                items(labels) { (lbl, clr) ->
                    val idx = labels.indexOfFirst { it.first == lbl }
                    val pct =
                        if (idx == 0) income / total * 100 else expenseByCategory[idx - 1].second / total * 100
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(12.dp)
                                .background(clr, shape = RoundedCornerShape(3.dp))
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "$lbl (${String.format("%.1f", pct)}%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

//@Composable
//fun MonthlyBarChartSection(
//    monthlyValues: List<Float> = listOf(
//        1200f, 900f, 1500f, 700f,
//        1100f, 1300f, 800f, 1000f,
//        950f, 1200f, 1050f, 1250f
//    ),
//    modifier: Modifier = Modifier
//) {
//    val monthNames = listOf(
//        stringResource(R.string.jan), stringResource(R.string.feb),
//        stringResource(R.string.mar), stringResource(R.string.apr),
//        stringResource(R.string.may), stringResource(R.string.jun),
//        stringResource(R.string.jul), stringResource(R.string.aug),
//        stringResource(R.string.sep), stringResource(R.string.oct),
//        stringResource(R.string.nov), stringResource(R.string.dec)
//    )
//    val maxValue = monthlyValues.maxOrNull() ?: 1f
//    val animatedFractions = monthlyValues.map { value ->
//        animateFloatAsState(
//            targetValue = (value / maxValue) * 0.8f,
//            animationSpec = tween(800, easing = EaseInOutCubic)
//        )
//    }
//    var tooltipIndex by remember { mutableStateOf<Int?>(null) }
//
//    Card(
//        shape = RoundedCornerShape(16.dp),
//        elevation = CardDefaults.cardElevation(4.dp),
//        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//    ) {
//        Column(Modifier.padding(16.dp)) {
//            Text(
//                text = stringResource(R.string.monthly_expenses_title),
//                style = MaterialTheme.typography.titleMedium,
//                color = colorScheme.primary
//            )
//            Spacer(Modifier.height(8.dp))
//
//            Box {
//                Canvas(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(180.dp)
//                ) {
//                    val path = Path().apply {
//                        monthlyValues.forEachIndexed { i, v ->
//                            val x = i * (size.width / (monthlyValues.size - 1))
//                            val y = size.height - (v / maxValue) * size.height
//                            if (i == 0) moveTo(x, y) else lineTo(x, y)
//                        }
//                    }
//                    drawPath(
//                        path = path,
//                        color = Color.White,
//
//                        style = Stroke(width = 2.dp.toPx())
//                    )
//                }
//
//                Row(
//                    Modifier
//                        .fillMaxWidth()
//                        .height(180.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.Bottom
//                ) {
//                    animatedFractions.forEachIndexed { idx, fracState ->
//                        Box(
//                            modifier = Modifier
//                                .weight(1f)
//                                .fillMaxHeight(fracState.value)
//                                .semantics {
//                                    val currency = (R.string.currency)
//                                    contentDescription = "${monthNames[idx]}: ${monthlyValues[idx].toInt()} $currency"
//
//
//                                }
//                                .pointerInput(Unit) {
//                                    detectTapGestures {
//                                        tooltipIndex = if (tooltipIndex == idx) null else idx
//                                    }
//                                }
//                        ) {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .background(
//                                        color = colorScheme.primaryContainer,
//                                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
//                                    )
//                            )
//                            Text(
//                                text = monthlyValues[idx].toInt().toString(),
//                                style = MaterialTheme.typography.bodySmall,
//                                modifier = Modifier
//                                    .align(Alignment.TopCenter)
//                                    .offset(y = -20.dp)
//                            )
//                            if (tooltipIndex == idx) {
//                                Card(
//                                    shape = RoundedCornerShape(8.dp),
//                                    elevation = CardDefaults.cardElevation(4.dp),
//                                    modifier = Modifier
//                                        .align(Alignment.Center)
//                                        .padding(4.dp)
//                                ) {
//                                    Text(
//                                        text = "${monthNames[idx]}: ${monthlyValues[idx].toInt()} ${
//                                            stringResource(
//                                                R.string.currency
//                                            )
//                                        }",
//                                        style = MaterialTheme.typography.bodySmall,
//                                        modifier = Modifier.padding(8.dp)
//                                    )
//                                }
//                            }
//                        }
//                        Spacer(modifier = Modifier.width(4.dp))
//                    }
//                }
//            }
//
//            Spacer(Modifier.height(12.dp))
//
//            Row(
//                Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                monthNames.forEach { month ->
//                    Text(
//                        text = month,
//                        style = MaterialTheme.typography.bodySmall,
//                        color = colorScheme.onSurface.copy(alpha = 0.7f),
//                        modifier = Modifier.weight(1f),
//                        maxLines = 1
//                    )
//                }
//            }
//        }
//    }
//}
