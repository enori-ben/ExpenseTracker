package com.example.expensetracker.view.mainscreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.repository.NavItem
import com.example.expensetracker.repository.Routes
import com.example.expensetracker.view.home.drawer.DrawerScreen
import com.example.expensetracker.view.home.homepage.HomeScreenContent
import com.example.expensetracker.view.scan.Scanning
import com.example.expensetracker.view.stats.StatsScreen
import com.example.expensetracker.view.TransactionViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(navController: NavController, transactionViewModel: TransactionViewModel) {

    val totalBalance by transactionViewModel.totalBalance.collectAsStateWithLifecycle()

    val viewModel: MainViewModel = viewModel()

    val navItems = listOf(
        NavItem("Home", painterResource(R.drawable.home)),
        NavItem("Scan", painterResource(R.drawable.scanning)),
        NavItem("Stats", painterResource(R.drawable.st)),
//        NavItem("profile", painterResource(R.drawable.ic_profile))
    )
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerScreen(
                onClose = { scope.launch { drawerState.close() } },
                navController = navController,
                totalBalance = totalBalance,
                drawerState = drawerState,
                scope = scope
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color(0xFF16292F),
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.6f),
                                start = Offset.Zero,
                                end = Offset(size.width, 0f),
                                strokeWidth = 10f
                            )
                        }
                ) {
                    NavigationBar(containerColor = Color.Black) {
                        navItems.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = viewModel.selectedIndex == index,
                                onClick = { viewModel.updateIndex(index) },
                                icon = {
                                    Icon(
                                        painter = item.icon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(38.dp),
                                        tint = if (viewModel.selectedIndex == index)
                                            Color(0xFFFFBB0C) else Color.Gray
                                    )
                                }

                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            when (viewModel.selectedIndex) {
                0 -> HomeScreenContent(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                    viewModel = transactionViewModel,
                    scope = scope,
                    drawerState = drawerState
                )

                1 -> Scanning(
                    onClose = { navController.navigate(Routes.MAIN_SCREEN) },
                    onConfirm = { navController.navigate(Routes.MAIN_SCREEN) },
                    viewModel = transactionViewModel
                )

                2 -> StatsScreen(navController, transactionViewModel)

//                3 -> ProfileScreen(navController)
            }
        }
    }
}



