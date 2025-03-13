package com.example.expensetracker.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.navigation.NavItem
import com.example.expensetracker.repository.Routes
import kotlinx.coroutines.launch

@Composable
fun MainScreen(navController: NavController) {
    val navItems = listOf(
        NavItem("Home", painterResource(R.drawable.home)),
        NavItem("Add", painterResource(R.drawable.add)),
        NavItem("Stats", painterResource(R.drawable.st))
    )
    var selectedIndex by remember { mutableIntStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerScreen(
                onClose = { scope.launch { drawerState.close() } },
                navController = navController
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color(0xFFF5F5F5),
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
                                selected = selectedIndex == index,
                                onClick = { selectedIndex = index },
                                icon = {
                                    Icon(
                                        painter = item.icon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(38.dp)
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFFFFBB0C),
                                    unselectedIconColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            ContentScreen(
                modifier = Modifier.padding(innerPadding),
                selectedIndex = selectedIndex,
                navController = navController,
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        }
    }
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    navController: NavController,
    onMenuClick: () -> Unit
) {
    when (selectedIndex) {
        0 -> HomeScreen(
            navController = navController,
            onMenuClick = onMenuClick
        )
        1 -> AddScreen()
        2 -> StatsScreen()
    }
}

